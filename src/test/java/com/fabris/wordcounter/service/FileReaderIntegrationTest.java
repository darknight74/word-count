package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@ActiveProfiles("integrationtest")
public class FileReaderIntegrationTest {

    @Autowired
    private FileReader fileReader;

    @Autowired
    private WordCounterFromQueue wordCounter;

    @Autowired
    private MongoClient mongoClient;

    private Logger logger = LoggerFactory.getLogger(FileReaderIntegrationTest.class);

    @BeforeEach
    private void setUp() {
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        database.getCollection(ApplicationSharedValues.LINES_COLLECTION).drop();
        database.getCollection(ApplicationSharedValues.WORDS_COLLECTION).drop();
        database.getCollection(ApplicationSharedValues.QUEUE_COLLECTION).drop();
    }

    @Test
    public void test() throws IOException, InterruptedException {
        fileReader.readFile("./src/test/resources/testFileSmall.txt");
        wordCounter.countWordsAndSave();
        List<Document> result = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        MongoCollection<Document> words = database.getCollection(ApplicationSharedValues.WORDS_COLLECTION);
        words.find()
                .sort(new Document().append("value", -1))
                .limit(3)
                .into(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("the", result.get(0).getString("_id"));
        assertEquals(10, result.get(0).getDouble("value"));
        assertEquals("and", result.get(2).getString("_id"));
        assertEquals(7, result.get(2).getDouble("value"));
        assertEquals("frankenstein", result.get(1).getString("_id"));
        assertEquals(8, result.get(1).getDouble("value"));
    }
}
