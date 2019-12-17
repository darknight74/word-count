package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Service;

@Service
public class ResourceCleanup {

    private MongoClient mongoClient;

    public ResourceCleanup(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void cleanUp() {
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        database.getCollection(ApplicationSharedValues.WORDS_COLLECTION).drop();
        database.getCollection(ApplicationSharedValues.LINES_COLLECTION).drop();
        database.getCollection(ApplicationSharedValues.QUEUE_COLLECTION).drop();
    }
}
