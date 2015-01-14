package de.lmu.playlist.service;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import de.lmu.playlist.domain.dao.TokenDao;
import de.lmu.playlist.domain.entity.SpotifyToken;
import de.lmu.playlist.facade.BadRequestException;

public class SpotifyServiceImpl implements SpotifyService {

    private final Client client = Client.create();

    private final TokenDao tokenDao;

    @Inject
    public SpotifyServiceImpl(final TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    @Override
    public SpotifyToken obtainTokenPair(String authCode) {
        MultivaluedMap<String, String> payload =  new MultivaluedMapImpl();
        payload.add("grant_type", "authorization_code");
        payload.add("code", authCode);
        payload.add("redirect_uri", SpotifyConstants.REDIRECT_URI);

        WebResource resource = client.resource(SpotifyConstants.SPOTIFY_URL);
        String idSecret = SpotifyConstants.CLIENT_ID + ":" + SpotifyConstants.CLIENT_SECRET;
        String idSecretEncoded = new String(Base64.encode(idSecret.getBytes()));
        resource.header("Authorization", "Basic " + idSecretEncoded);
        resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        SpotifyToken token = null;
        try {
            token = resource.post(SpotifyToken.class, payload);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }

        return token;
    }

    @Override
    public SpotifyToken refreshTokenPair(String refreshToken) {
        MultivaluedMap<String, String> payload =  new MultivaluedMapImpl();
        payload.add("grant_type", "refresh_token");
        payload.add("refresh_token", refreshToken);

        WebResource resource = client.resource(SpotifyConstants.SPOTIFY_URL);
        String idSecret = SpotifyConstants.CLIENT_ID + ":" + SpotifyConstants.CLIENT_SECRET;
        String idSecretEncoded = new String(Base64.encode(idSecret.getBytes()));

        resource.header("Authorization", "Basic " + idSecretEncoded);
        SpotifyToken token = null;
        try {
            token = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(SpotifyToken.class, payload);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }

        return token;
    }
}
