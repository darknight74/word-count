package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import com.fabris.wordcounter.configuration.RabbitConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.MapReduceAction;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class WordCounter {

    private MongoClient mongoClient;

    private Logger logger = LoggerFactory.getLogger(WordCounter.class);

    public WordCounter(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @RabbitListener(queues = "${rabbit.queue}")
    public void countWordsAndSave(String lineId) {
        logger.debug("Received message for line " + lineId);
        LocalDateTime start = LocalDateTime.now();

        MongoDatabase database = mongoClient.getDatabase(ApplicationSharedValues.DATABASE_NAME);
        MongoCollection<Document> lines = database.getCollection(ApplicationSharedValues.LINES_COLLECTION);
        String mapFunction = "function() {\n" +
                "    var line = this.content;\n" +
                "    if (line) {\n" +
                "        var words = line.toLowerCase().match(/\\w+/g);\n" +
                "        if (words == null) { return; }\n" +
                "        for (var i = words.length - 1; i >= 0; i--) {\n" +
                "            if (words[i])  {\n" +
                "               emit(words[i], 1);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "};";

        String reduceFunction = "function(key, values) {\n" +
               "    return Array.sum(values)\n" +
                "}";


        Document filter = new Document().append("_id", new ObjectId(lineId));
        lines.mapReduce(mapFunction, reduceFunction)
                .action(MapReduceAction.REDUCE)
                .filter(filter)
                .collectionName(ApplicationSharedValues.WORDS_COLLECTION)
                .toCollection();

        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.debug("Counting took " + duration.toMillis() + " ms");
    }
}
