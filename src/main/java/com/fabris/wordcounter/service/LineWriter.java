package com.fabris.wordcounter.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LineWriter {

    private Logger logger = LoggerFactory.getLogger(LineWriter.class);

    private MongoClient mongoClient;

    public LineWriter(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public ObjectId writeLine(String line) {
        MongoDatabase database = mongoClient.getDatabase("wordcount");
        MongoCollection<Document> lines = database.getCollection("lines");
        Document documentLine = new Document("content", line)
                .append("time", new Date());
        lines.insertOne(documentLine);
        ObjectId documentLineId = documentLine.getObjectId("_id");
        logger.debug("ObjectId of new document: " + documentLineId);
        return documentLineId;
    }

}
