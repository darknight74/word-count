package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest()
@ActiveProfiles("integrationtest")
public class ApplicationIntegrationTest {

    @Autowired
    private FileReader fileReader;

    @Autowired
    private OutputGenerator outputGenerator;

    @Autowired
    private MongoClient mongoClient;

    private Logger logger = LoggerFactory.getLogger(ApplicationIntegrationTest.class);

    @BeforeEach
    private void setUp() {
        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        database.getCollection(ApplicationSharedValues.LINES_COLLECTION).drop();
        database.getCollection(ApplicationSharedValues.WORDS_COLLECTION).drop();
    }

    @Test
    public void test() throws IOException, InterruptedException {
        fileReader.readFile("./src/test/resources/testFileSmall.txt");
        Thread.sleep(5000);
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
        Map<String, Document> calculation = outputGenerator.calculateStats();
        assertEquals("1", calculation.get("min").getString("_id"));
        assertEquals(1D, calculation.get("min").get("value"));
        assertEquals("the", calculation.get("max").getString("_id"));
        assertEquals(10D, calculation.get("max").get("value"));
    }
}
