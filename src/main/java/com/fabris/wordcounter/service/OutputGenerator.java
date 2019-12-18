package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.fabris.wordcounter.configuration.RabbitConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OutputGenerator {

    private MongoClient mongoClient;
    private Client rabbitAdminClient;

    private Logger logger = LoggerFactory.getLogger(OutputGenerator.class);

    public OutputGenerator(MongoClient mongoClient, Client rabbitAdminClient) {
        this.mongoClient = mongoClient;
        this.rabbitAdminClient = rabbitAdminClient;
    }

    public Map<String, Document> calculateStats() throws InterruptedException {

        MongoDatabase database = mongoClient
                .getDatabase(ApplicationSharedValues.DATABASE_NAME);

        Map<String, Document> result = new HashMap<>();
        boolean queueIsNotEmpty = true;
        while (queueIsNotEmpty) {
            QueueInfo queueInfo = rabbitAdminClient.getQueue("/", RabbitConfiguration.LINES_TO_BE_COUNTED_QUEUE);
            if (queueInfo.getMessageStats().getBasicDeliver() == queueInfo.getMessageStats().getBasicPublish()) {
                logger.info("Queue is empty");
                queueIsNotEmpty = false;
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
                Thread.sleep(1000);
            }
        }
        return result;
    }
}
