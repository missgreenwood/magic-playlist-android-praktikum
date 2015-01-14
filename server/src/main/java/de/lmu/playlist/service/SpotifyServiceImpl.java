package de.lmu.playlist.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import de.lmu.playlist.domain.entity.SpotifyToken;
import de.lmu.playlist.facade.BadRequestException;

public class SpotifyServiceImpl implements SpotifyService {

    private final Client client = Client.create();

    @Override
    public SpotifyToken obtainTokenPair(String authCode) {
        MultivaluedMap<String, String> payload = new MultivaluedMapImpl();
        client.addFilter(new LoggingFilter(System.out));
        payload.add("grant_type", "authorization_code");
        payload.add("code", authCode);
        payload.add("redirect_uri", SpotifyConstants.REDIRECT_URI);
        payload.add("client_id", SpotifyConstants.CLIENT_ID);
        payload.add("client_secret", SpotifyConstants.CLIENT_SECRET);

        SpotifyToken token;
        try {
            WebResource resource = client.resource(SpotifyConstants.SPOTIFY_URL);
            token = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(SpotifyToken.class, payload);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }

        return token;
    }

    @Override
    public SpotifyToken refreshTokenPair(String refreshToken) {
        MultivaluedMap<String, String> payload = new MultivaluedMapImpl();
        payload.add("grant_type", "refresh_token");
        payload.add("refresh_token", refreshToken);
        payload.add("client_id", SpotifyConstants.CLIENT_ID);
        payload.add("client_secret", SpotifyConstants.CLIENT_SECRET);

        SpotifyToken token;
        try {
            WebResource resource = client.resource(SpotifyConstants.SPOTIFY_URL);
            token = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(SpotifyToken.class, payload);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }

        return token;
    }
}
