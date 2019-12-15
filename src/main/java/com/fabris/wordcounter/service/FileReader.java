package com.fabris.wordcounter.service;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
public class FileReader {

    private Logger logger = LoggerFactory.getLogger(FileReader.class);

    private LineWriterFuture lineWriter;
    private WordCounterFuture wordCounter;

    public FileReader(LineWriterFuture lineWriter, WordCounterFuture wordCounter) {
        this.lineWriter = lineWriter;
        this.wordCounter = wordCounter;
    }

    public void readFile(String filePath) throws IOException {
        LocalDateTime start = LocalDateTime.now();
        Files
                .lines(Path.of(filePath))
                .parallel()
                .forEach(line -> {
                    try {
                        ObjectId newLineId = lineWriter.writeLine(line).get();
                        wordCounter.countWordsAndSave(newLineId);
                    } catch (InterruptedException e) {
                       logger.error("Interruption during async call", e);
                    } catch (ExecutionException e) {
                        logger.error("Execution error during async call", e);
                    }
                });
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.info("Reading took " + duration.toMillis() + " ms");
    }
}
