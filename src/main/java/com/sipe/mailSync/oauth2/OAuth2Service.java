package com.sipe.mailSync.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OAuth2Service {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Service.class);

    private final RestTemplate restTemplate;

    @Value("${app.topic}")
    private String topic;

    public OAuth2Service(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getEmail(final String idToken) {
        String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idToken;
        ResponseEntity<EmailResponse> response = restTemplate.getForEntity(url, EmailResponse.class);
        return response.getBody().getEmail();
    }

    public void registerGmailWatchEvent(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + accessToken);

        WatchRequest watchRequest = new WatchRequest(
                topic,
                List.of("INBOX"),
                "INCLUDE"
        );
        HttpEntity<WatchRequest> request = new HttpEntity<>(watchRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/gmail/v1/users/me/watch",
                HttpMethod.POST,
                request,
                String.class
        );

        log.info("발행성공 {}" ,response );
    }

}
