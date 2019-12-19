package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.RabbitConfiguration;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CheckingOutputGenerator {

    private OutputGenerator outputGenerator;
    private Client rabbitAdminClient;
    private RabbitConfiguration configuration;

    private Logger logger = LoggerFactory.getLogger(CheckingOutputGenerator.class);

    public CheckingOutputGenerator(OutputGenerator outputGenerator, Client rabbitAdminClient, RabbitConfiguration configuration) {
        this.outputGenerator = outputGenerator;
        this.rabbitAdminClient = rabbitAdminClient;
        this.configuration = configuration;
    }

    public Map<String, Document> calculateStats() throws InterruptedException {
        boolean queueIsNotEmpty = true;
        Map<String, Document> result = new HashMap<>();
        while (queueIsNotEmpty) {
            QueueInfo queueInfo = rabbitAdminClient.getQueue("/", configuration.getQueue());
            if (queueInfo.getMessagesReady() == 0) {
                logger.info("Queue is empty");
                queueIsNotEmpty = false;
                result = outputGenerator.calculateStats();
                TimeUnit.SECONDS.sleep(1);
            }
        }
        return result;
    }
}
