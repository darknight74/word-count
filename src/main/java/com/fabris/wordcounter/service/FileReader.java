package com.fabris.wordcounter.service;

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

    public FileReader(LineWriter lineWriter) {
        this.lineWriter = lineWriter;
    }

    public Boolean readFile(String filePath) throws IOException {
        LocalDateTime start = LocalDateTime.now();
        Files
                .lines(Path.of(filePath))
                .parallel()
                .forEach(line -> {
                    if (!line.isBlank()) {
                        logger.debug("Invoking linewriter for line " + line);
                        lineWriter.writeLine(line);
                    }
                });
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.info("Reading took " + duration.toMillis() + " ms");
        return true;
    }
}
