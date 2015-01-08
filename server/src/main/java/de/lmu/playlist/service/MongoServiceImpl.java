package de.lmu.playlist.service;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class MongoServiceImpl implements MongoService {

    private final MongoClient mongoClient;

    private final DB db;

    public MongoServiceImpl() throws UnknownHostException {
        mongoClient = new MongoClient("localhost", 27017);
        db = mongoClient.getDB("magic-playlist");
    }

    @Override
    public DB getDB() {
        return db;
    }
}
