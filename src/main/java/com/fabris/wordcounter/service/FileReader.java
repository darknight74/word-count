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

@Service
public class FileReader {

    private Logger logger = LoggerFactory.getLogger(FileReader.class);

    private LineWriter lineWriter;
    private WordCounter wordCounter;

    public FileReader(LineWriter lineWriter, WordCounter wordCounter) {
        this.lineWriter = lineWriter;
        this.wordCounter = wordCounter;
    }

    public void readFile(String filePath) throws IOException {
        LocalDateTime start = LocalDateTime.now();
        Files
                .lines(Path.of(filePath))
                .parallel()
                .forEach(line -> {
                    ObjectId newLineId = lineWriter.writeLine(line);
                    wordCounter.countWordsAndSave(newLineId);
                });
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.info("Reading took " + duration.toMillis() + " ms");

    }
}
