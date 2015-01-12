package de.lmu.playlist.domain.dao;

import de.lmu.playlist.domain.entity.SpotifyToken;

public interface TokenDao {

    public SpotifyToken saveToken(SpotifyToken spotifyToken);

    public SpotifyToken update(SpotifyToken spotifyToken);
}
