package com.fabris.wordcounter;

import com.fabris.wordcounter.service.FileReader;
import com.mongodb.client.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordCounterApplication implements ApplicationRunner {

    private final FileReader fileReader;
    private Logger logger = LoggerFactory.getLogger(WordCounterApplication.class);

    public WordCounterApplication(FileReader fileReader, MongoClient mongoClient) {
        this.fileReader = fileReader;
    }

    public static void main(String[] args) {
        SpringApplication.run(WordCounterApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        fileReader.readFile(args.getOptionValues("source").get(0));
    }
}
