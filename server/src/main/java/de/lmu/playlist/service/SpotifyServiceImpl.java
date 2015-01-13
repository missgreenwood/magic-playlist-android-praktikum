package de.lmu.playlist.service;

import com.google.gson.Gson;
import com.google.inject.Inject;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import de.lmu.playlist.domain.dao.TokenDao;
import de.lmu.playlist.domain.entity.SpotifyToken;

public class SpotifyServiceImpl implements SpotifyService {

    private final TokenDao tokenDao;

    private final HttpClient client;

    private final Gson gson;

    @Inject
    public SpotifyServiceImpl(final TokenDao tokenDao) {
        this.tokenDao = tokenDao;
        this.client = new DefaultHttpClient();
        this.gson = new Gson();
    }

    @Override
    public SpotifyToken obtainTokenPair(String authCode) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("code", authCode));
        params.add(new BasicNameValuePair("redirect_uri", SpotifyConstants.REDIRECT_URI));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));

        HttpPost request = new HttpPost(SpotifyConstants.SPOTIFY_URL);
        request.addHeader("Authorization", "Basic " + SpotifyConstants.CLIENT_ID + ":" + SpotifyConstants.CLIENT_SECRET);
        request.setHeader(HTTP.CONTENT_TYPE, "application/json");
        // request.setEntity();

        return tokenDao.saveToken(null);
    }

    @Override
    public SpotifyToken refreshTokenPair(String refreshToken) {
        return tokenDao.update(null);
    }
}
