package de.lmu.playlist.service;

import com.mongodb.DB;

/**
 * @author martin
 *         <p/>
 *         simple service encapsulating mongo db.
 */
public interface MongoService {

    /**
     * @return the DB object needed by all DAOs to operate on their collections.
     */
    public DB getDB();
}
