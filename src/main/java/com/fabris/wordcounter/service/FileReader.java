package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class FileReader {

    private Logger logger = LoggerFactory.getLogger(FileReader.class);

    private LineWriter lineWriter;
    private ApplicationSharedValues configuration;

    public FileReader(LineWriter lineWriter, ApplicationSharedValues configuration) {
        this.lineWriter = lineWriter;
        this.configuration = configuration;
    }

    public Boolean readFile(String filePath) throws IOException {
        LocalDateTime start = LocalDateTime.now();
        Files
                .lines(Path.of(filePath))
                .parallel()
                .forEach(line -> {
                    if (!line.isBlank()) {
                        logger.debug("Invoking linewriter for line " + line);
                        try {
                            lineWriter.writeLine(line);
                            TimeUnit.MILLISECONDS.sleep(configuration.getReadWaitMillis());
                        } catch (InterruptedException e) {
                            logger.error("Line thread interrupted", e);
                        } catch (ExecutionException e) {
                            logger.error("Line thread interrupted", e);
                        }
                    }
                });
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        logger.info("Reading took " + duration.toMillis() + " ms");
        return true;
    }
}
