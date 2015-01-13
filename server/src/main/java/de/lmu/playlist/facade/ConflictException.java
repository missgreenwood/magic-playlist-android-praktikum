package de.lmu.playlist.facade;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ConflictException extends WebApplicationException {
    private static final long serialVersionUID = 1L;

    public ConflictException() {
        this(null, null);
    }

    public ConflictException(URI location) {
        this(location, null);
    }

    public ConflictException(URI location, Object entity) {
        super(Response.status(Response.Status.CONFLICT).location(location).entity(entity).build());
    }
}
