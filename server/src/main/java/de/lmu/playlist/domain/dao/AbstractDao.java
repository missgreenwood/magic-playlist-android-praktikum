package de.lmu.playlist.domain.dao;

import com.mongodb.DBCollection;

import org.mongojack.JacksonDBCollection;

import de.lmu.playlist.service.MongoService;

abstract class AbstractDao<T> {

    private JacksonDBCollection<T, String> dbCollection;

    public AbstractDao(final MongoService mongoService, Class clazz, String collectionName) {
        DBCollection collection = mongoService.getDB().getCollection(collectionName);
        dbCollection = JacksonDBCollection.wrap(collection, clazz, String.class);
        ensureIndices();
    }

    protected JacksonDBCollection<T, String> getDBCollection() {
        return dbCollection;
    }

    protected abstract void ensureIndices();

    protected void drop() {
        dbCollection.drop();
        ensureIndices();
    }
}
