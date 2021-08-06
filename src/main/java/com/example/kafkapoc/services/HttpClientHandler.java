package com.example.kafkapoc.services;

import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.classic.ConnectionBackoffStrategy;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.reactor.DefaultConnectingIOReactor;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class HttpClientHandler {
    private final Logger logger = LoggerFactory.getLogger(HttpClientHandler.class);
    @Value("${endpoint.url}")
    private String endpointURL;

    @Value("${endpoint.max-retry}")
    private int maxRetryCount;

    @Value("${endpoint.timeout}")
    private long timeout;

    @Value("${endpoint.retry-interval}")
    private long retryInterval;

    @Value("${endpoint.connection-pool}")
    private int connectionPool;

    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;

    public void sendRequest(JSONObject jsonObject) {
        try {
            if(Objects.isNull(httpClient)){
                createClient();
                createRequestConfig();
            }
            HttpPost request = new HttpPost(endpointURL);
            request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            request.setEntity(new StringEntity(jsonObject.toString()));
            request.setConfig(requestConfig);
            PostSenderThread postSenderThread = new PostSenderThread(httpClient,request);
            postSenderThread.start();
           // postSenderThread.join();
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    private void createClient(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(connectionPool);
        httpClient = HttpClientBuilder.create()
                .setRetryStrategy(new HttpRequestRetryStrategy() {
                    @Override
                    public boolean retryRequest(HttpRequest httpRequest, IOException e, int i, HttpContext httpContext) {
                        logger.warn("Retry " + i + " of " + maxRetryCount);
                        if(e instanceof SocketTimeoutException){
                            logger.warn("Socket Timeout");
                            return false;
                        }
                        try {
                            Thread.sleep(retryInterval);
                        }catch (InterruptedException interruptedException){
                            logger.error(interruptedException.getMessage());
                        }
                        return i <= maxRetryCount;
                    }

                    @Override
                    public boolean retryRequest(HttpResponse httpResponse, int i, HttpContext httpContext) {
                        return false;
                    }

                    @Override
                    public TimeValue getRetryInterval(HttpResponse httpResponse, int i, HttpContext httpContext) {
                        logger.info("Get Retry Interval");
                        return TimeValue.ofMilliseconds(retryInterval);
                    }
                })
                .setConnectionManager(connectionManager)
                .build();
        }

    private void createRequestConfig(){
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout, TimeUnit.MILLISECONDS)
                .setConnectionRequestTimeout(timeout, TimeUnit.MILLISECONDS)
                .setResponseTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }
}
