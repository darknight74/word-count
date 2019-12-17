package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OutputGenerator {

    private MongoClient mongoClient;

    private Logger logger = LoggerFactory.getLogger(OutputGenerator.class);

    public OutputGenerator(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Map<String, Document> calculateStats() throws InterruptedException {

        MongoDatabase database = mongoClient
                .getDatabase(ApplicationSharedValues.DATABASE_NAME);
        MongoCollection<Document> queue = database
                .getCollection(ApplicationSharedValues.QUEUE_COLLECTION);
        Map<String, Document> result = new HashMap<>();
        boolean queueIsNotEmpty = true;
        while (queueIsNotEmpty) {
            List<Document> queueResult = queue.find(Filters.eq("elaborationTime", null))
                    .into(new ArrayList<>());
            if (queueResult.isEmpty()) {
                logger.info("Queue is empty");
                queueIsNotEmpty = false;
                MongoCollection<Document> words = database
                        .getCollection(ApplicationSharedValues.WORDS_COLLECTION);

                Optional<Document> maxResult = Optional.of(words.find()
                        .sort(Sorts.descending("value"))
                        .limit(1)
                        .first());
                Optional<Document> minResult = Optional.of(words.find()
                        .sort(Sorts.ascending("value"))
                        .limit(1)
                        .first());
                result.put("max", maxResult.orElseGet(Document::new));
                result.put("min", minResult.orElseGet(Document::new));
                Thread.sleep(1000);
            }
        }
        return result;
    }
}
