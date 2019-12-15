package com.fabris.wordcounter.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoBeanFactory {

    private final MongoConfiguration configuration;

    public MongoBeanFactory(MongoConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://" + configuration.getHost() + ":" + configuration.getPort());
    }
}
