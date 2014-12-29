package de.lmu.playlist.domain.dao;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import de.lmu.playlist.domain.entity.Playlist;
import de.lmu.playlist.service.MongoService;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import java.util.List;

public class PlaylistDaoImpl implements PlaylistDao {

    private JacksonDBCollection<Playlist, String> dbCollection;

    @Inject
    public PlaylistDaoImpl(final MongoService mongoService) {
        DBCollection collection = mongoService.getDB().getCollection("playlists");
        dbCollection = JacksonDBCollection.wrap(collection, Playlist.class, String.class);
    }

    @Override
    public void savePlaylist(Playlist playlist) {
        getDBCollection().insert(playlist);
    }

    @Override
    public Iterable<Playlist> findPlaylist(String author) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(Playlist.AUTHOR, author);
        DBCursor dbCursor = getDBCollection().find(dbObject);
        return dbCursor.toArray();
    }

    private JacksonDBCollection getDBCollection() {
        return dbCollection;
    }
}
