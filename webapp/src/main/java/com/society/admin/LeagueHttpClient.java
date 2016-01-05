package com.society.admin;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.society.admin.security.CookieAuth;
import com.society.admin.security.CookieContext;
import feign.Client;
import feign.Request;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import feign.Request;
import feign.Request.Options;
import feign.Response;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static feign.Util.CONTENT_ENCODING;
import static feign.Util.CONTENT_LENGTH;
import static feign.Util.ENCODING_GZIP;

@Component
public class LeagueHttpClient extends Client.Default {
    static final String CONTENT_ENCODING = "Content-Encoding";
    static final String GZIP_ENCODING = "gzip";
    static Logger logger = LoggerFactory.getLogger(LeagueHttpClient.class);

    private  SSLSocketFactory sslContextFactory = null;
    private  HostnameVerifier hostnameVerifier = null;

    public LeagueHttpClient() {
        super(null,null);
    }
    Cache<String,String> cachedResponse;

    public LeagueHttpClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
        super(sslContextFactory, hostnameVerifier);
    }

    @PostConstruct
    public void init() {
         cachedResponse = CacheBuilder.newBuilder()
                 .maximumSize(1000)
                 .initialCapacity(500)
                 .expireAfterAccess(5, TimeUnit.MINUTES).build();
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        /*
        if (request.method().equals(RequestMethod.GET.name()) && cachedResponse.getIfPresent(request.url()) != null) {
            logger.info("serving cache");
            //logger.info("hits " + cachedResponse.stats().toString());
            return Response.create(200, "", Collections.emptyMap() , cachedResponse.getIfPresent(request.url()).getBytes());
        }
        if (!request.method().equals(RequestMethod.GET.name())) {
            cachedResponse.invalidateAll();
        }
         */
        HttpURLConnection connection = convertAndSend(request, options);
        Response response = convertResponse(connection);
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
        if (response.status() == 200 && request.method().equals(RequestMethod.GET.name()) && (!request.url().endsWith("/api/user"))) {
            //logger.info("storing cache");
            //String resp = IOUtils.toString(response.body().asInputStream());
            //response.body().close();
            //cachedResponse.put(request.url(),resp);
            //response.body().close();
            return response;
            //return Response.create(200, response.reason(), response.headers(), response);
        }

        if (response.status() != 200)  {
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
                contentEncodingValues != null && contentEncodingValues.contains("deflate");

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

    Response convertResponse(HttpURLConnection connection) throws IOException {
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
            stream = connection.getErrorStream();
        } else {
            stream = connection.getInputStream();
        }

        return Response.create(status, reason, headers, stream, length);
    }

    /*
    HttpURLConnection convertAndSend(Request request, Options options) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(request.url()).openConnection();
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection sslCon = (HttpsURLConnection) connection;
            //sslCon.setSSLSocketFactory(sslContextFactory.get());
            //sslCon.setHostnameVerifier(hostnameVerifier.get());
        }
        connection.setConnectTimeout(options.connectTimeoutMillis());
        connection.setReadTimeout(options.readTimeoutMillis());
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(request.method());

        boolean gzipEncodedRequest =
                request.headers().containsKey(CONTENT_ENCODING) &&
                        request.headers().get(CONTENT_ENCODING).contains(GZIP_ENCODING);

        Integer contentLength = null;
        for (String field : request.headers().keySet()) {
            for (String value : request.headers().get(field)) {
                if (field.equals(CONTENT_LENGTH)) {
                    if (!gzipEncodedRequest) {
                        contentLength = Integer.valueOf(value);
                        connection.addRequestProperty(field, value);
                    }
                } else {
                    connection.addRequestProperty(field, value);
                }
            }
        }

        if (request.body() != null) {
            if (contentLength != null) {
                connection.setFixedLengthStreamingMode(contentLength);
            } else {
                connection.setChunkedStreamingMode(8196);
            }
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            try {
                if (gzipEncodedRequest) {
                    out = new GZIPOutputStream(out);
                }
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

    Response convertResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();

        Map<String, Collection<String>> headers = new LinkedHashMap<String, Collection<String>>();
        for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
            // response message
            if (field.getKey() != null)
                headers.put(field.getKey(), field.getValue());
        }

        Integer length = connection.getContentLength();
        if (length == -1 || length == 0)
            length = null;
        InputStream stream;
        if (status >= 400) {
            stream = connection.getErrorStream();
        } else {
            stream = connection.getInputStream();
        }

        return Response.create(status, reason, headers, status != 200 ? stream : new GZIPInputStream(stream), length);
    }
    */
}
