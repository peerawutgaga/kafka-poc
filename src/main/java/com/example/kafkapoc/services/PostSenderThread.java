package com.example.kafkapoc.services;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class PostSenderThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(PostSenderThread.class);
    private CloseableHttpClient httpClient;
    private HttpPost request;


    public PostSenderThread(CloseableHttpClient httpClient, HttpPost request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            logger.info("Send: "+EntityUtils.toString(request.getEntity()));
            CloseableHttpResponse response = httpClient.execute(request);
            logger.info("Receive: "+EntityUtils.toString(response.getEntity()));
            response.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(ExceptionUtils.getStackTrace(e));
        }

    }

}
