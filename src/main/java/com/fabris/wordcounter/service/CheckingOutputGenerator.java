package com.fabris.wordcounter.service;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.event.ListenerContainerIdleEvent;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CheckingOutputGenerator {

    private OutputGenerator outputGenerator;

    private Logger logger = LoggerFactory.getLogger(CheckingOutputGenerator.class);

    public CheckingOutputGenerator(OutputGenerator outputGenerator) {
        this.outputGenerator = outputGenerator;
    }

    @EventListener(condition = "event.listenerId.startsWith('counter-')")
    public void calculateStats(ListenerContainerIdleEvent event)  {
        Map<String, Document> result = outputGenerator.calculateStats();

        logger.info("Result " + result);
    }
}
