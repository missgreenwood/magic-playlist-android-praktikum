package de.lmu.playlist.facade;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.lmu.playlist.domain.entity.Playlist;

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

    @PUT
    @Path("/like")
    @Consumes(MediaType.APPLICATION_JSON)
    public void likePlaylist(Playlist playlist);

    @DELETE
    @Path("/clean")
    public void clean();

    @GET
    @Path("/spotify/get_tokens")
    public void getTokens(@QueryParam("auth_code") String authCode);

    @GET
    @Path("/spotify/refresh_token")
    @Produces(MediaType.APPLICATION_JSON)
    public void refreshToken(@QueryParam("refresh_token") String refreshToken);
    // BasicNameValuePair authCodePair = new BasicNameValuePair("refresh_token", refreshToken);
}
