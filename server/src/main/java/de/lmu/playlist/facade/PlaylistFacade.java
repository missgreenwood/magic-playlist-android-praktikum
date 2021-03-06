package de.lmu.playlist.facade;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.lmu.playlist.domain.entity.Playlist;
import de.lmu.playlist.domain.entity.SpotifyToken;

/**
 * @author martin
 *         <p/>
 *         this facade contains all end points for incoming rest calls.
 */
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
    @Path("/find/name")
    @Produces(MediaType.APPLICATION_JSON)
    public Playlist findPlaylist(@QueryParam("name") String name);

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Playlist> findPlaylists(@QueryParam("genre") String genre, @QueryParam("artist") String artist);

    @POST
    @Path("/similar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Playlist> findSimilar(Playlist playlist);

    @GET
    @Path("/like")
    @Produces(MediaType.APPLICATION_JSON)
    public Playlist likePlaylist(@QueryParam("name") String name);

    @DELETE
    @Path("/clean")
    public void clean();

    @GET
    @Path("/spotify/get_tokens")
    @Produces(MediaType.APPLICATION_JSON)
    public SpotifyToken getTokens(@QueryParam("auth_code") String authCode);

    @GET
    @Path("/spotify/refresh_token")
    @Produces(MediaType.APPLICATION_JSON)
    public SpotifyToken refreshToken(@QueryParam("refresh_token") String refreshToken);
}
