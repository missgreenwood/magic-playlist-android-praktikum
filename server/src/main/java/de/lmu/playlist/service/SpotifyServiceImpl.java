package de.lmu.playlist.service;

import com.google.inject.Inject;

import de.lmu.playlist.domain.dao.TokenDao;
import de.lmu.playlist.domain.entity.SpotifyToken;

public class SpotifyServiceImpl implements SpotifyService {

    private TokenDao tokenDao;

    @Inject
    public SpotifyServiceImpl(final TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    @Override
    public SpotifyToken obtainToken(String authCode) {
        return tokenDao.saveToken(null);
    }

    @Override
    public SpotifyToken refreshTokenPair(String refreshToken) {
        return tokenDao.update(null);
    }
}
