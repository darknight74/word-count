package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LineWriterTest {

    private MongoClient mongoClient = MongoClients.create();

    private LineWriter service = new LineWriter(mongoClient);

    @BeforeEach
    private void setUp() {
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        database.getCollection(ApplicationSharedValues.LINES_COLLECTION).drop();
        database.getCollection(ApplicationSharedValues.QUEUE_COLLECTION).drop();
    }

    @Test
    void writeLine() {
        String line = "This is a line of text";
        ObjectId id = service.writeLine(line);
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        MongoCollection<Document> linesCollection = database.getCollection(ApplicationSharedValues.LINES_COLLECTION);
        MongoCollection<Document> queueCollection = database.getCollection(ApplicationSharedValues.QUEUE_COLLECTION);
        List<Document> lines = new ArrayList<>();
        linesCollection.find().into(lines);
        assertFalse(lines.isEmpty());
        assertEquals(1, lines.size());
        Document lineDocument = lines.get(0);
        assertEquals(line, lineDocument.getString("content"));
        assertTrue(lineDocument.getDate("time").before(new Date()));
        assertEquals(id, lineDocument.getObjectId("_id"));
        List<Document> queueEntries = new ArrayList<>();
        queueCollection.find().into(queueEntries);
        assertFalse(queueEntries.isEmpty());
        assertEquals(1, queueEntries.size());
        Document queueEntry = queueEntries.get(0);
        assertNull(queueEntry.getDate("elaborationTime"));
        assertEquals(id, queueEntry.getObjectId("lineId"));
    }
}