package de.lmu.playlist.service;

import de.lmu.playlist.domain.entity.SpotifyToken;

public interface SpotifyService {

    /**
     * Requests a token pair from spotify with the provided auth code.
     *
     * @param authCode the auth code
     * @return the persisted token pair AND the expiry date
     */
    public SpotifyToken obtainTokenPair(String authCode);

    /**
     * Refreshes the access token that belongs to the pair of the provided refresh token.
     *
     * @param refreshToken the refresh token for which its access token will be refreshed
     * @return the access token AND the expiry date
     */
    public SpotifyToken refreshTokenPair(String refreshToken);
}
