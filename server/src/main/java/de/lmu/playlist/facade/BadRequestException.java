package de.lmu.playlist.facade;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author martin
 *         <p/>
 *         this exception will answer as a 400 Bad request. use accordingly.
 */
public class BadRequestException extends WebApplicationException {
    private static final long serialVersionUID = 1L;

    public BadRequestException() {
        this(null, null);
    }

    public BadRequestException(URI location) {
        this(location, null);
    }

    public BadRequestException(URI location, Object entity) {
        super(Response.status(Response.Status.BAD_REQUEST).location(location).entity(entity).build());
    }
}

