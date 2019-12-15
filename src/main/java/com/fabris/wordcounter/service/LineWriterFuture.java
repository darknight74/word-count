package com.fabris.wordcounter.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class LineWriterFuture {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private LineWriter lineWriter;

    public LineWriterFuture(LineWriter lineWriter) {
        this.lineWriter = lineWriter;
    }

    public Future<ObjectId> writeLine(String line) {
        return executor.submit(() -> lineWriter.writeLine(line));
    }
}
