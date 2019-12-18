package com.fabris.wordcounter;

import com.fabris.wordcounter.service.FileReader;
import com.fabris.wordcounter.service.OutputGenerator;
import com.fabris.wordcounter.service.ResourceCleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;

@SpringBootApplication
public class WordCounterApplication implements ApplicationRunner {

    private final FileReader fileReader;
    private final OutputGenerator outputGenerator;
    private final ResourceCleanup resourceCleanup;

    private Logger logger = LoggerFactory.getLogger(WordCounterApplication.class);

    public WordCounterApplication(FileReader fileReader,
                                  OutputGenerator outputGenerator,
                                  ResourceCleanup resourceCleanup) {
        this.fileReader = fileReader;
        this.outputGenerator = outputGenerator;
        this.resourceCleanup = resourceCleanup;
    }

    public static void main(String[] args) {
        SpringApplication.run(WordCounterApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (args.containsOption("source")) {
            LocalDateTime start = LocalDateTime.now();
            resourceCleanup.cleanUp();

            fileReader.readFile(args.getOptionValues("source").get(0));
            logger.info("stats: " + outputGenerator.calculateStats());

            LocalDateTime end = LocalDateTime.now();
            Duration duration = Duration.between(start, end);
            logger.info("Application took " + duration.toMillis() + " ms");
        }
    }
}
