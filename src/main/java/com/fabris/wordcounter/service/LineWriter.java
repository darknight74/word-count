package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.fabris.wordcounter.configuration.RabbitConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class LineWriter {

    private Logger logger = LoggerFactory.getLogger(LineWriter.class);

    private MongoClient mongoClient;
    private AmqpTemplate messagingTemplate;

    public LineWriter(MongoClient mongoClient, AmqpTemplate messagingTemplate) {
        this.mongoClient = mongoClient;
        this.messagingTemplate = messagingTemplate;
    }

    public ObjectId writeLine(String line) {
        LocalDateTime start = LocalDateTime.now();
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);

        MongoCollection<Document> lines = database.getCollection(ApplicationSharedValues.LINES_COLLECTION);
        Document documentLine = new Document("content", line)
                .append("time", new Date());
        lines.insertOne(documentLine);
        ObjectId documentLineId = documentLine.getObjectId("_id");

        logger.debug("ObjectId of new document: " + documentLineId);

        messagingTemplate.send(
                RabbitConfiguration.LINES_TO_BE_COUNTED_QUEUE,
                MessageBuilder
                        .withBody(documentLineId.toString().getBytes())
                        .build());
        logger.debug("Finished writing line " + line + " with ObjectId " + documentLineId);
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.info("Write line took " + duration.toMillis() + " ms");
        return documentLineId;
    }

}
