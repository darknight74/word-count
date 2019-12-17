package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class WordCounterFromQueue {

    private WordCounter wordCounter;
    private MongoClient mongoClient;

    private Logger logger = LoggerFactory.getLogger(WordCounterFromQueue.class);

    public WordCounterFromQueue(WordCounter wordCounter, MongoClient mongoClient) {
        this.wordCounter = wordCounter;
        this.mongoClient = mongoClient;
    }

    public Boolean countWordsAndSave() throws InterruptedException {
        logger.debug("Getting available lines to be read from queue");
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        MongoCollection<Document> queue = database.getCollection(ApplicationSharedValues.QUEUE_COLLECTION);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.sort(new Document("time", 1));
        boolean queueIsNotEmpty = true;
        while (queueIsNotEmpty) {
            Document queueEntry = queue.findOneAndUpdate(
                    Filters.eq("elaborationTime", null),
                    Updates.currentDate("elaborationTime"),
                    options);
            if (queueEntry != null) {
                logger.debug("Document found. Counting words");
                LocalDateTime start = LocalDateTime.now();
                wordCounter.countWordsAndSave(queueEntry.getObjectId("lineId"));
                LocalDateTime end = LocalDateTime.now();
                Duration duration = Duration.between(start, end);
                logger.info("Counting took " + duration.toMillis() + " ms");
            } else {
                logger.debug("Document not found");
                queueIsNotEmpty = false;
            }
        }
        return true;
    }
}
