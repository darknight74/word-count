package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.fabris.wordcounter.configuration.KafkaConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LineWriterTest {

    private MongoClient mongoClient = MongoClients.create();

    private KafkaTemplate messagingTemplate = mock(KafkaTemplate.class);
    private KafkaConfiguration configuration = new KafkaConfiguration();
    private LineWriter service = new LineWriter(mongoClient, messagingTemplate, configuration);

    @BeforeEach
    private void setUp() {
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        database.getCollection(ApplicationSharedValues.LINES_COLLECTION).drop();
    }

    @Test
    void writeLine() throws ExecutionException, InterruptedException {
        String line = "This is a line of text";
        ObjectId id = service.writeLine(line);
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        MongoCollection<Document> linesCollection = database.getCollection(ApplicationSharedValues.LINES_COLLECTION);
        List<Document> lines = new ArrayList<>();
        linesCollection.find().into(lines);
        assertFalse(lines.isEmpty());
        assertEquals(1, lines.size());
        Document lineDocument = lines.get(0);
        assertEquals(line, lineDocument.getString("content"));
        assertTrue(lineDocument.getDate("time").before(new Date()));
        assertEquals(id, lineDocument.getObjectId("_id"));
        verify(messagingTemplate, new Times(1))
                .send(
                        eq(configuration.getDestination()),
                        eq(id.toString()));
    }
}