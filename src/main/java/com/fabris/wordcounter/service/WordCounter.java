package com.fabris.wordcounter.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.MapReduceAction;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class WordCounter {

    private MongoClient mongoClient;

    public WordCounter(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void countWordsAndSave(ObjectId newLineId) {
        MongoDatabase database = mongoClient.getDatabase("wordcount");
        MongoCollection<Document> lines = database.getCollection("lines");
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
                "    var count = 0;\n" +
                "    values.forEach(function(v) {\n" +
                "        count +=v;\n" +
                "    });\n" +
                "    return count;\n" +
                "}";

        Document filter = new Document().append("_id", newLineId);
        lines.mapReduce(mapFunction, reduceFunction)
                .action(MapReduceAction.MERGE)
                .filter(filter)
                .collectionName("words")
                .toCollection();
    }
}
