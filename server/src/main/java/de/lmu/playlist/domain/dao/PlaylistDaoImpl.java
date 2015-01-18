package de.lmu.playlist.domain.dao;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

import org.mongojack.DBQuery;

import java.util.List;

import de.lmu.playlist.domain.entity.Playlist;
import de.lmu.playlist.service.MongoService;

/**
 * @author martin
 */
public class PlaylistDaoImpl extends AbstractDao<Playlist> implements PlaylistDao {

    @Inject
    public PlaylistDaoImpl(final MongoService mongoService) {
        super(mongoService, Playlist.class, "playlist");
    }

    @Override
    protected void ensureIndices() {
        getDBCollection().ensureIndex(new BasicDBObject(Playlist.NAME, 1), new BasicDBObject("unique", true));
        getDBCollection().ensureIndex(Playlist.GENRE);
    }

    @Override
    public void savePlaylist(Playlist playlist) {
        getDBCollection().insert(playlist);
    }

    @Override
    public Playlist findPlaylist(String name) {
        return getDBCollection().findOne(DBQuery.is(Playlist.NAME, name));
    }

    @Override
    public List<Playlist> findAllPlaylists() {
        return getDBCollection().find().toArray();
    }

    @Override
    public List<Playlist> findPlaylists(String genre) {
        BasicDBObject dbObject = new BasicDBObject();
        if (genre != null && !genre.isEmpty()) {
            dbObject.append(Playlist.GENRE, genre);
        }
        return getDBCollection().find(dbObject).toArray();
    }

    @Override
    public Playlist update(Playlist playlist) {
        getDBCollection().update(DBQuery.is(Playlist.NAME, playlist.getName()), playlist);
        return findPlaylist(playlist.getName());
    }

    @Override
    public void drop() {
        super.drop();
    }
}
