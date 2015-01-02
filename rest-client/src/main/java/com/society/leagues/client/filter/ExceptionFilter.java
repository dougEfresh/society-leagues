package com.society.leagues.client.filter;

import com.society.leagues.client.api.domain.TokenHeader;
import com.society.leagues.client.exception.BadRequest;
import com.society.leagues.client.exception.ServerError;
import com.society.leagues.client.exception.Unauthorized;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class ExceptionFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        if (responseContext.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
            String errMsg = String.format("Unauthorized request to %s  " +
                            "Make sure you have a valid token header set (%s). " +
                            "Login at %s to set a token",
                    requestContext.getUri(), TokenHeader.NAME, "/api/auth");
            throw new Unauthorized(errMsg);
        }

        if (responseContext.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            String errMsg = String.format("Forbidden request to %s " ,
                    requestContext.getUri());
            throw new Unauthorized(errMsg);
        }

         if (responseContext.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
             String errMsg = String.format("Bad Request sent to server: %s  (%s)" ,
                    requestContext.getUri(),
                     responseContext.getStatusInfo().getStatusCode());
            throw new BadRequest(errMsg);
         }

        if (responseContext.getStatusInfo().getFamily() == Response.Status.Family.SERVER_ERROR) {
            String errMsg = String.format("Server error request for %s. Code: %s  Msg: %s" ,
                    requestContext.getUri(),
                    responseContext.getStatusInfo().getStatusCode(),
                    responseContext.getStatusInfo().getReasonPhrase());
            throw new ServerError(errMsg);
        }

    }
}
