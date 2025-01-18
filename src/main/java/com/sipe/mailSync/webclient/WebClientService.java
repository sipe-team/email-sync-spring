package com.sipe.mailSync.webclient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class WebClientService {

    private final WebClient webClient;

    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T, S> Mono<T> sendPostReturnOnlyBody(String baseUrl, String uri, HttpHeaders headers, S reqBody, Class<T> responseType) {
        return webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                .headers(reqHeaders -> reqHeaders.addAll(headers))
                .bodyValue(reqBody)
                .retrieve()
                .bodyToMono(responseType)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(Mono::error);
    }

    public <T, S> Mono<ResponseEntity<T>> sendPostReturnResponseEntity(String baseUrl, String uri, HttpHeaders headers, S reqBody, Class<T> responseType) {
        return webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                .headers(reqHeaders -> reqHeaders.addAll(headers))
                .bodyValue(reqBody)
                .retrieve()
                .toEntity(responseType)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(Mono::error);
    }

    public <T, S> Mono<ResponseEntity<T>> sendPostReturnResponseEntity(String url, HttpHeaders headers, S reqBody, Class<T> responseType) {
        return webClient.mutate()
                .baseUrl(url)
                .build()
                .post()
                .headers(reqHeaders -> reqHeaders.addAll(headers))
                .bodyValue(reqBody)
                .retrieve()
                .toEntity(responseType)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(Mono::error);
    }

    public <T> Mono<T> sendGet(String baseUrl, String uri, HttpHeaders headers, Class<T> responseType) {
        return webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .headers(reqHeaders -> reqHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(responseType)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(Mono::error);
    }

}
