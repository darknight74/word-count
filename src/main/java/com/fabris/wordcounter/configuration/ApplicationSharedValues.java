package com.fabris.wordcounter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wordcount")
public class ApplicationSharedValues {

    public static final String DATABASE_NAME = "wordcount";

    public static final String LINES_COLLECTION = "lines";

    public static final String WORDS_COLLECTION = "words";

    public static final String QUEUE_COLLECTION = "queue";

    private int readWaitMillis = 0;

    public int getReadWaitMillis() {
        return readWaitMillis;
    }

    public void setReadWaitMillis(int readWaitMillis) {
        this.readWaitMillis = readWaitMillis;
    }
}
