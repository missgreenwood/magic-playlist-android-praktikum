package de.lmu.playlist.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import de.lmu.playlist.domain.entity.SpotifyToken;
import de.lmu.playlist.facade.BadRequestException;

/**
 * @author martin
 */
public class SpotifyServiceImpl implements SpotifyService {

    private final Client client = Client.create();

    @Override
    public SpotifyToken obtainTokenPair(String authCode) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        client.addFilter(new LoggingFilter(System.out));
        params.add("grant_type", "authorization_code");
        params.add("code", authCode);
        params.add("redirect_uri", SpotifyConstants.REDIRECT_URI);
        params.add("client_id", SpotifyConstants.CLIENT_ID);
        params.add("client_secret", SpotifyConstants.CLIENT_SECRET);

        SpotifyToken token;
        try {
            token = client.resource(SpotifyConstants.SPOTIFY_URL)
                    .queryParams(params)
                    .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                    .post(SpotifyToken.class);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            throw new BadRequestException();
        }

        return token;
    }

    @Override
    public SpotifyToken refreshTokenPair(String refreshToken) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        params.add("client_id", SpotifyConstants.CLIENT_ID);
        params.add("client_secret", SpotifyConstants.CLIENT_SECRET);

        SpotifyToken token;
        try {
            token = client.resource(SpotifyConstants.SPOTIFY_URL)
                    .queryParams(params)
                    .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                    .post(SpotifyToken.class);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            throw new BadRequestException();
        }

        return token;
    }
}
