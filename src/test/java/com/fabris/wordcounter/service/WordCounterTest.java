package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.fabris.wordcounter.configuration.RabbitConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class WordCounterTest {

    private MongoClient mongoClient = MongoClients.create();

    private WordCounter wordCounter = new WordCounter(mongoClient);
    private AmqpTemplate messagingTemplate = mock(AmqpTemplate.class);
    private RabbitConfiguration configuration = new RabbitConfiguration();
    private LineWriter lineWriter = new LineWriter(mongoClient, messagingTemplate, configuration);

    @BeforeEach
    private void setUp() {
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        database.getCollection(ApplicationSharedValues.LINES_COLLECTION).drop();
        database.getCollection(ApplicationSharedValues.WORDS_COLLECTION).drop();
    }

    @Test
    void countWordsAndSave() throws InterruptedException {
        MongoDatabase database = mongoClient.getDatabase("wordcount");
        String firstLineToCount = "This is one line with more than one word we want to count";
        ObjectId lineId = lineWriter.writeLine(firstLineToCount);
        wordCounter.countWordsAndSave(lineId.toString());
        MongoCollection<Document> wordsCollection = database.getCollection("words");
        List<Document> wordsDocuments = new ArrayList<>();
        wordsCollection.find().into(wordsDocuments);
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
        String secondLineToCount = "Another one one";
        ObjectId secondLineId = lineWriter.writeLine(secondLineToCount);
        wordCounter.countWordsAndSave(secondLineId.toString());
        wordsDocuments.clear();
        wordsCollection.find().into(wordsDocuments);
        assertEquals(13, wordsDocuments.size());
        wordsDocuments.forEach(document -> {
            switch (document.getString("_id")) {
                case "another":
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
                    assertEquals(4, document.getDouble("value"));
                    break;
            }
        });
    }
}