package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.fabris.wordcounter.configuration.RabbitConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OutputGenerator {

    private MongoClient mongoClient;

    private Logger logger = LoggerFactory.getLogger(OutputGenerator.class);

    public OutputGenerator(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Map<String, Document> calculateStats() {

        MongoDatabase database = mongoClient
                .getDatabase(ApplicationSharedValues.DATABASE_NAME);

        Map<String, Document> result = new HashMap<>();
        MongoCollection<Document> words = database
                .getCollection(ApplicationSharedValues.WORDS_COLLECTION);

        Optional<Document> maxResult = Optional.of(words.find()
                .sort(Sorts.orderBy(Sorts.descending("value"), Sorts.ascending("_id")))
                .limit(1)
                .first());
        Optional<Document> minResult = Optional.of(words.find()
                .sort(Sorts.ascending("value", "_id"))
                .limit(1)
                .first());
        result.put("max", maxResult.orElseGet(Document::new));
        result.put("min", minResult.orElseGet(Document::new));
        return result;
    }
}
