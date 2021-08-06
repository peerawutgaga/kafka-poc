package com.example.kafkapoc.services;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PostSenderThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(PostSenderThread.class);
    private CloseableHttpClient httpClient;
    private HttpPost request;


    public PostSenderThread(CloseableHttpClient httpClient, HttpPost request) {
        this.httpClient = httpClient;
        this.request = request;
    }
    @Override
    public void run(){
        try{
            CloseableHttpResponse response = httpClient.execute(request);
            logger.info(EntityUtils.toString(response.getEntity()));
            response.close();
        }catch (Exception e){
            logger.error(e.getMessage());
        }

    }

}
