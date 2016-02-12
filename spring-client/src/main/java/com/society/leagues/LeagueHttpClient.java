package com.society.leagues;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.society.leagues.security.CookieContext;
import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static feign.Util.CONTENT_LENGTH;
import static feign.Util.ENCODING_GZIP;

@Component
public class LeagueHttpClient extends Client.Default {
    static final String CONTENT_ENCODING = "Content-Encoding";
    static final String GZIP_ENCODING = "gzip";
    static final String DEFLATE = "deflate";
    static Logger logger = LoggerFactory.getLogger(LeagueHttpClient.class);

    private SSLSocketFactory sslContextFactory = null;
    private HostnameVerifier hostnameVerifier = null;

    Cache<String, CachedResponse> cachedResponse;

    public LeagueHttpClient() {
        super(null, null);
    }

    public LeagueHttpClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
        super(sslContextFactory, hostnameVerifier);
    }

    @PostConstruct
    public void init() {
        cachedResponse = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .initialCapacity(500)
                .expireAfterAccess(30, TimeUnit.MINUTES).build();

    }

    @Scheduled(fixedRate = 1000 * 60 * 1)
    public void stats() {
        logger.info("Cache Stat Entries " + cachedResponse.size());
    }

    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void statsSize() {
        logger.info("Cache Stat Size " + cachedResponse.size());
        Map<String, CachedResponse> cache = cachedResponse.asMap();
        for (String s : cache.keySet()) {
            logger.info(String.format("%s\t%s", cache.get(s).response.length + "", s));
        }
    }


    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        if (request.method().equals(RequestMethod.GET.name()) && cachedResponse.getIfPresent(request.url()) != null) {
            logger.info("serving cache");
            CachedResponse cached = cachedResponse.getIfPresent(request.url());
            if (cached != null) {
                ByteArrayInputStream in = new ByteArrayInputStream(cached.response);
                return Response.create(200, "", Collections.emptyMap(), new GZIPInputStream(in), cached.length);
            }
        }
        if (!request.method().equals(RequestMethod.GET.name())) {
            cachedResponse.invalidateAll();
        }

        HttpURLConnection connection = convertAndSend(request, options);
        Response response = convertResponse(connection, request);
        if (response.headers().containsKey("Set-Cookie")) {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context instanceof CookieContext) {
                CookieContext cookieContext = (CookieContext) context;
                if (!cookieContext.isCookieChange()) {
                    cookieContext.setCookieChange(true);
                    cookieContext.setNewCookies(response.headers().get("Set-Cookie"));
                }
            }
        }
        if (response.status() != 200) {
            cachedResponse.invalidateAll();
        }
        return response;
    }

    HttpURLConnection convertAndSend(Request request, Options options) throws IOException {
        final HttpURLConnection
                connection =
                (HttpURLConnection) new URL(request.url()).openConnection();
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection sslCon = (HttpsURLConnection) connection;
            if (sslContextFactory != null) {
                sslCon.setSSLSocketFactory(sslContextFactory);
            }
            if (hostnameVerifier != null) {
                sslCon.setHostnameVerifier(hostnameVerifier);
            }
        }
        connection.setConnectTimeout(options.connectTimeoutMillis());
        connection.setReadTimeout(options.readTimeoutMillis());
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(request.method());

        Collection<String> contentEncodingValues = request.headers().get(CONTENT_ENCODING);
        boolean
                gzipEncodedRequest =
                contentEncodingValues != null && contentEncodingValues.contains(ENCODING_GZIP);
        boolean
                deflateEncodedRequest =
                contentEncodingValues != null && contentEncodingValues.contains(DEFLATE);

        boolean hasAcceptHeader = false;
        Integer contentLength = null;
        for (String field : request.headers().keySet()) {
            if (field.equalsIgnoreCase("Accept")) {
                hasAcceptHeader = true;
            }
            for (String value : request.headers().get(field)) {
                if (field.equals(CONTENT_LENGTH)) {
                    if (!gzipEncodedRequest && !deflateEncodedRequest) {
                        contentLength = Integer.valueOf(value);
                        connection.addRequestProperty(field, value);
                    }
                } else {
                    connection.addRequestProperty(field, value);
                }
            }
        }
        // Some servers choke on the default accept string.
        if (!hasAcceptHeader) {
            connection.addRequestProperty("Accept", "*/*");
        }

        if (request.body() != null) {
            if (contentLength != null) {
                connection.setFixedLengthStreamingMode(contentLength);
            } else {
                connection.setChunkedStreamingMode(8196);
            }
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            if (gzipEncodedRequest) {
                out = new GZIPOutputStream(out);
            } else if (deflateEncodedRequest) {
                out = new DeflaterOutputStream(out);
            }
            try {
                out.write(request.body());
            } finally {
                try {
                    out.close();
                } catch (IOException suppressed) { // NOPMD
                }
            }
        }
        return connection;
    }

    Response convertResponse(HttpURLConnection connection, Request request) throws IOException {
        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();

        Map<String, Collection<String>> headers = new LinkedHashMap<String, Collection<String>>();
        for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
            // response message
            if (field.getKey() != null) {
                headers.put(field.getKey(), field.getValue());
            }
        }

        Integer length = connection.getContentLength();
        if (length == -1) {
            length = null;
        }
        InputStream stream;
        if (status >= 400) {
            return Response.create(status, reason, headers, connection.getErrorStream(), length);
        } else {
            stream = connection.getInputStream();
        }
        if (request.headers().containsKey("X-Cache") || !request.method().equals(HttpMethod.GET.name())) {
            // Don't cache this response
            return Response.create(status, reason, headers, new GZIPInputStream(stream), length);
        }
        if (status == 200) {
            byte[] resp = compress(IOUtils.toByteArray(new GZIPInputStream(stream)));
            cachedResponse.put(request.url(), new CachedResponse(resp, length));
            stream = new ByteArrayInputStream(resp);
            return Response.create(status, reason, headers, new GZIPInputStream(stream), length);
        }
        if (status == 404) {
            return Response.create(status, "Not Found: " + request.method(), headers, null, 0);
        }
        return Response.create(status, reason, headers, stream, length);
    }

    static class CachedResponse {
        final byte[] response;
        final Integer length;

        public CachedResponse(byte[] response, Integer length) {
            this.response = response;
            this.length = length;
        }
    }

    public static byte[] compress(final byte[] str) throws IOException {
        if ((str == null) || (str.length == 0)) {
            return null;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str);
        gzip.close();
        return obj.toByteArray();
    }
}
