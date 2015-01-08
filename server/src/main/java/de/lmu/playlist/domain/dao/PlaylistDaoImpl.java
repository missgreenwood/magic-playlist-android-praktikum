package de.lmu.playlist.domain.dao;

import com.google.inject.Inject;
import com.mongodb.DBCollection;

import de.lmu.playlist.domain.entity.Playlist;
import de.lmu.playlist.service.MongoService;

import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

public class PlaylistDaoImpl implements PlaylistDao {

    private JacksonDBCollection<Playlist, String> dbCollection;

    @Inject
    public PlaylistDaoImpl(final MongoService mongoService) {
        DBCollection collection = mongoService.getDB().getCollection("playlist");
        dbCollection = JacksonDBCollection.wrap(collection, Playlist.class, String.class);
    }

    private JacksonDBCollection<Playlist, String> getDBCollection() {
        return dbCollection;
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
    public void drop() {
        dbCollection.drop();
    }
}
