package com.example.kafkapoc.services;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ApacheHttpHandler {
    @Value("${endpoint.url}")
    private String httpMockUrl;
    @Value("${endpoint.maxretry}")
    private int maxRetry;
    @Value("${endpoint.timeout}")
    private int timeout;
    public void sendRequest(JSONObject jsonObject ){
        try {
            HttpPost postRequest = new HttpPost(httpMockUrl);
            postRequest.setEntity(new StringEntity(jsonObject.toString()));
            postRequest.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
            postRequest.addHeader(HttpHeaders.TIMEOUT,String.valueOf(timeout));
            CloseableHttpClient httpClient = HttpClientBuilder.create().setRetryHandler(new HttpRequestRetryHandler() {
                @Override
                public boolean retryRequest(IOException e, int count, HttpContext httpContext) {
                    System.out.println("Retry "+count+" of "+maxRetry);
                    return count <= maxRetry;
                }
            }).setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                @Override
                public boolean retryRequest(HttpResponse httpResponse, int count, HttpContext httpContext) {
                    System.out.println("Send "+count+" of "+maxRetry);
                    return count <= maxRetry && httpResponse.getStatusLine().getStatusCode() >= 500;
                }

                @Override
                public long getRetryInterval() {
                    return timeout;
                }
            }).build();
            CloseableHttpResponse httpResponse = httpClient.execute(postRequest);
            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
            httpClient.close();
            httpResponse.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
