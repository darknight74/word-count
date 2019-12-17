package com.fabris.wordcounter;

import com.fabris.wordcounter.service.FileReader;
import com.fabris.wordcounter.service.OutputGenerator;
import com.fabris.wordcounter.service.ResourceCleanup;
import com.fabris.wordcounter.service.WordCounterFromQueue;
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
    private final WordCounterFromQueue wordCounter;
    private final OutputGenerator outputGenerator;
    private final ResourceCleanup resourceCleanup;

    private ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();

    private Logger logger = LoggerFactory.getLogger(WordCounterApplication.class);

    public WordCounterApplication(FileReader fileReader,
                                  WordCounterFromQueue wordCounter,
                                  OutputGenerator outputGenerator,
                                  ResourceCleanup resourceCleanup) {
        this.fileReader = fileReader;
        this.wordCounter = wordCounter;
        this.outputGenerator = outputGenerator;
        this.resourceCleanup = resourceCleanup;
    }

    public static void main(String[] args) {
        SpringApplication.run(WordCounterApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Callable<Boolean> fileReaderRunnable = () -> {
            try {
                return fileReader.readFile(args.getOptionValues("source").get(0));
            } catch (IOException e) {
                logger.error("Error when reading source", e);
            }
            return false;
        };

        Callable<Boolean> wordCounterRunnable = () -> {
            try {
                return wordCounter.countWordsAndSave();
            } catch (InterruptedException e) {
                logger.error("Error when counting words", e);
            }
            return false;
        };

        if (args.containsOption("source")) {
            LocalDateTime start = LocalDateTime.now();
            resourceCleanup.cleanUp();

            Future<Boolean> fileReaderFuture = threadPool.submit(fileReaderRunnable);
            Future<Boolean> wordCounterFuture = scheduledThreadPool.schedule(wordCounterRunnable, 5, TimeUnit.SECONDS);

            if (fileReaderFuture.get()
                    && wordCounterFuture.get()) {
                logger.info("stats: " + outputGenerator.calculateStats());
            } else {
                logger.error("Some error occurred during elaboration");
            }
            LocalDateTime end = LocalDateTime.now();
            Duration duration = Duration.between(start, end);
            logger.info("Application took " + duration.toMillis() + " ms");
        } else {
            LocalDateTime start = LocalDateTime.now();
            Future<Boolean> wordCounterFuture = scheduledThreadPool.schedule(wordCounterRunnable, 5, TimeUnit.SECONDS);
            if (wordCounterFuture.get()) {
                LocalDateTime end = LocalDateTime.now();
                Duration duration = Duration.between(start, end);
                logger.info("Application took " + duration.toMillis() + " ms");
            }
        }
    }
}
