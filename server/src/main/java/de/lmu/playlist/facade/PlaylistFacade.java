package de.lmu.playlist.facade;

import de.lmu.playlist.domain.entity.Playlist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/playlist")
public interface PlaylistFacade {

    @GET
    @Path("/alive")
    public String alive();

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addPlaylist(Playlist playlist);

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<Playlist> findPlaylists(@QueryParam("author") String author);
}
