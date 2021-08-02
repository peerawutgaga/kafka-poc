package com.example.kafkapoc.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class JavaNetHttpHandler {
    @Value("${endpoint.url}")
    private String httpMockUrl;
    @Value("${endpoint.maxretry}")
    private static int maxRetry;
    @Value("${endpoint.timeout}")
    private static int timeout;
    public void sendRequest(JSONObject jsonObject ){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(httpMockUrl))
                    .timeout(Duration.ofMillis(3000))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build();
            CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, handler)
                    .thenComposeAsync(r -> tryResend(client, request, handler, 1, r));
            response.thenApply(HttpResponse::statusCode)
                    .thenAccept(System.out::println);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static <T> CompletableFuture<HttpResponse<T>>
    tryResend(HttpClient client, HttpRequest request, HttpResponse.BodyHandler<T> handler,
              int count, HttpResponse<T> resp) {
        if (resp.statusCode() == 200 || count >= 3) {
            return CompletableFuture.completedFuture(resp);
        } else {
            return client.sendAsync(request, handler)
                    .thenComposeAsync(r -> tryResend(client, request, handler, count+1, r));
        }
    }
}
