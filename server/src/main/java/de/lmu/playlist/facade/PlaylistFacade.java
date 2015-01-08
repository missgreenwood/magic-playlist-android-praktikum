package de.lmu.playlist.facade;

import de.lmu.playlist.domain.entity.Playlist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/playlist")
public interface PlaylistFacade {

    /**
     * @return if the server is up and running, this method simply returns the string "alive".
     */
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
    public Playlist findPlaylistByName(@QueryParam("name") String name);
}
