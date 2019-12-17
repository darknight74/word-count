package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class LineWriter {

    private Logger logger = LoggerFactory.getLogger(LineWriter.class);

    private MongoClient mongoClient;

    public LineWriter(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
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

        MongoCollection<Document> queue = database.getCollection(ApplicationSharedValues.QUEUE_COLLECTION);
        Document queueEntry = new Document("lineId", documentLineId)
                .append("time", new Date())
                .append("elaborationTime", null);
        queue.insertOne(queueEntry);
        logger.debug("Finished writing line " + line + " with ObjectId " + documentLineId);
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.info("Write line took " + duration.toMillis() + " ms");
        return documentLineId;
    }

}
