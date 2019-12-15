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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordCounterTest {

    private MongoClient mongoClient = MongoClients.create();

    private WordCounter service = new WordCounter(mongoClient);
    private LineWriter lineWriter = new LineWriter(mongoClient);

    @BeforeEach
    private void setUp() {
        MongoDatabase database = mongoClient.getDatabase("wordcount");
        database.getCollection("lines").drop();
        database.getCollection("words").drop();
    }

    @Test
    void countWordsAndSave() throws Exception {
        MongoDatabase database = mongoClient.getDatabase("wordcount");
        String lineToCount = "This is one line with more than one word we want to count";
        ObjectId lineObjectId = lineWriter.writeLine(lineToCount);
        service.countWordsAndSave(lineObjectId);
        MongoCollection<Document> wordsCollection = database.getCollection("words");
        List<Document> wordsDocuments = new ArrayList<>();
        wordsCollection.find(new Document()).into(wordsDocuments);
        assertEquals(12, wordsDocuments.size());
        wordsDocuments.forEach(document -> {
            switch (document.getString("_id")) {
                case "this":
                case "is":
                case "line":
                case "with":
                case "more":
                case "than":
                case "word":
                case "we":
                case "want":
                case "to":
                case "count":
                    assertEquals(1, document.getDouble("value"));
                    break;
                case "one":
                    assertEquals(2, document.getDouble("value"));
                    break;
            }
        });
    }
}