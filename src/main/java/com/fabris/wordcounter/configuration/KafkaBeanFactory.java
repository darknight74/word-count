package com.fabris.wordcounter.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaBeanFactory {

//    @Bean
    public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setIdleEventInterval(60000L);
        return factory;
    }

    @Bean
    public NewTopic createTopic(KafkaConfiguration configuration) {
        return TopicBuilder.name(configuration.getDestination())
                .build();
    }
}
