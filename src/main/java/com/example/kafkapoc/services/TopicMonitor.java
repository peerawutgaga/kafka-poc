package com.example.kafkapoc.services;

import org.apache.avro.generic.GenericRecord;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletableFuture;


@Service
public class TopicMonitor {
    @Autowired
    ApacheHttpHandler apacheHttpHandler;

    @Autowired
    JavaNetHttpHandler javaNetHttpHandler;

    @KafkaListener(topics = "#{'${io.confluent.developer.config.topic.name}'}")
    public void listen(ConsumerRecord<String, GenericRecord> data) {

        String firstname = data.value().get("firstname").toString();
        String lastname = data.value().get("lastname").toString();
        System.out.println(firstname + " "+lastname);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstname",firstname);
        jsonObject.put("lastname",lastname);
       // javaNetHttpHandler.sendRequest(jsonObject);
        apacheHttpHandler.sendRequest(jsonObject);
    }



}
