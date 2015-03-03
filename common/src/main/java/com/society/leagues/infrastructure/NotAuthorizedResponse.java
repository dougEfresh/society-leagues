package com.society.leagues.infrastructure;

import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SuppressWarnings("unused")
public class NotAuthorizedResponse {
    final OutboundJaxrsResponse response;

    public NotAuthorizedResponse(ContainerRequestContext requestContext) {
        OutboundMessageContext context = new OutboundMessageContext();
        context.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        context.setEntity("Unauthorized Access to " + requestContext.getUriInfo());
        response = new OutboundJaxrsResponse(Response.Status.FORBIDDEN,context);
    }

    public OutboundJaxrsResponse getResponse() {
        return response;
    }
}
