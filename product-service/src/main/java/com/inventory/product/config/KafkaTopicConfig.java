package com.inventory.product.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic productCreatedTopic(
            @Value("${app.kafka.topics.product-created}") String productCreatedTopic
    ) {
        return TopicBuilder.name(productCreatedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
