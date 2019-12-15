package com.fabris.wordcounter.service;

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
        MongoDatabase database = mongoClient.getDatabase("wordcount");
        database.getCollection("lines").drop();
    }

    @Test
    void writeLine() {
        String line = "This is a line of text";
        ObjectId id = service.writeLine(line);
        MongoDatabase database = mongoClient.getDatabase("wordcount");
        MongoCollection linesCollection = database.getCollection("lines");
        List<Document> result = new ArrayList<>();
        linesCollection.find(new Document()).into(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(line, result.get(0).getString("content"));
        assertTrue(result.get(0).getDate("time").before(new Date()));
        assertEquals(id, result.get(0).getObjectId("_id"));
    }
}