package com.example.kafkapoc;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KafkaPocApplication {
	// injected from application.properties
	@Value("${topic.name}")
	private String topicName;

	@Value("${topic.partitions}")
	private int numPartitions;

	@Value("${topic.replicas}")
	private int replicas;

	@Bean
	NewTopic clientTopic() {
		return new NewTopic(topicName, numPartitions, (short) replicas);
	}
	public static void main(String[] args) {
		SpringApplication.run(KafkaPocApplication.class, args);
	}

}
