package de.lmu.playlist.domain.dao;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

import org.mongojack.DBQuery;
import org.mongojack.WriteResult;

import de.lmu.playlist.domain.entity.SpotifyToken;
import de.lmu.playlist.service.MongoService;

public class TokenDaoImpl extends AbstractDao<SpotifyToken> implements TokenDao {

    @Inject
    public TokenDaoImpl(final MongoService mongoService) {
        super(mongoService, SpotifyToken.class, "token");
    }

    @Override
    protected void ensureIndices() {
        getDBCollection().ensureIndex(new BasicDBObject(SpotifyToken.REFRESH_TOKEN, 1), new BasicDBObject("unique", true));
        getDBCollection().ensureIndex(SpotifyToken.ACCESS_TOKEN);
    }

    @Override
    public SpotifyToken saveToken(SpotifyToken spotifyToken) {
        return getDBCollection().insert(spotifyToken).getSavedObject();
    }

    @Override
    public SpotifyToken update(SpotifyToken spotifyToken) {
        return getDBCollection().update(DBQuery.is(SpotifyToken.REFRESH_TOKEN, spotifyToken.getRefreshToken()), spotifyToken).getSavedObject();
    }
}
