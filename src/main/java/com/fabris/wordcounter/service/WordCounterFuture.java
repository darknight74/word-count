package com.fabris.wordcounter.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class WordCounterFuture {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private WordCounter wordCounter;

    public WordCounterFuture(WordCounter wordCounter) {
        this.wordCounter = wordCounter;
    }

    public Future<?> countWordsAndSave(ObjectId newLineId) {
        return executor.submit(() -> wordCounter.countWordsAndSave(newLineId));
    }
}
