package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.fabris.wordcounter.configuration.KafkaConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class LineWriter {

    private Logger logger = LoggerFactory.getLogger(LineWriter.class);

    private MongoClient mongoClient;
    private KafkaConfiguration configuration;
    private KafkaTemplate<String, String> kafkaTemplate;

    public LineWriter(MongoClient mongoClient, KafkaTemplate messagingTemplate, KafkaConfiguration configuration) {
        this.mongoClient = mongoClient;
        this.kafkaTemplate = messagingTemplate;
        this.configuration = configuration;
    }

    public ObjectId writeLine(String line) throws ExecutionException, InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);

        MongoCollection<Document> lines = database.getCollection(ApplicationSharedValues.LINES_COLLECTION);
        Document documentLine = new Document("content", line)
                .append("time", new Date());
        lines.insertOne(documentLine);
        ObjectId documentLineId = documentLine.getObjectId("_id");

        logger.debug("ObjectId of new document: " + documentLineId);

        kafkaTemplate.send(configuration.getDestination(), documentLineId.toString()).get();

        logger.debug("Finished writing line " + line + " with ObjectId " + documentLineId);
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.debug("Write line took " + duration.toMillis() + " ms");
        return documentLineId;
    }

}
