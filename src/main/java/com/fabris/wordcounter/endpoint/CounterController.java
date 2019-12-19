package com.fabris.wordcounter.endpoint;

import com.fabris.wordcounter.service.OutputGenerator;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CounterController {

    private OutputGenerator generator;

    public CounterController(OutputGenerator generator) {
        this.generator = generator;
    }

    @GetMapping("/count")
    public Map<String, Document> count() {
        return generator.calculateStats();
    }
}
