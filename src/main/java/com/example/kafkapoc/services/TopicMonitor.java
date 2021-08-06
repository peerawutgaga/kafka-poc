package com.example.kafkapoc.services;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TopicMonitor {
    private final Logger logger = LoggerFactory.getLogger(TopicMonitor.class);

    @Autowired
    private HttpClientHandler httpClientHandler;

    @KafkaListener(topics = "#{'${io.confluent.developer.config.topic.name}'}")
    public void listen(ConsumerRecord<String, GenericRecord> data) {

        String firstname = data.value().get("firstname").toString();
        String lastname = data.value().get("lastname").toString();
        logger.info("Pop: "+firstname+" "+lastname);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstname",firstname);
        jsonObject.put("lastname",lastname);
        httpClientHandler.sendRequest(jsonObject);
    }



}
