package com.sipe.mailSync.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/oauth2")
@RestController
public class OAuth2Controller {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);

    @Value("${app.client-id}")
    private String clientId;
    @Value("${app.client-secret}")
    private String clientSecret;
    private final RestTemplate restTemplate;
    private final OAuth2Service oAuth2Service;
    private final OAuth2InMemoryRepository oAuth2InMemoryRepository;

    public OAuth2Controller(final RestTemplate restTemplate, final OAuth2Service oAuth2Service, final OAuth2InMemoryRepository oAuth2InMemoryRepository) {
        this.restTemplate = restTemplate;
        this.oAuth2Service = oAuth2Service;
        this.oAuth2InMemoryRepository = oAuth2InMemoryRepository;
    }

    @GetMapping
    public ResponseEntity<String> test(@RequestParam String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("redirect_uri", "http://localhost:8080/oauth2");
        map.add("grant_type", "authorization_code");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                request,
                AccessTokenResponse.class
        );

        AccessTokenResponse accessTokenResponse = response.getBody();
        String email = oAuth2Service.getEmail(accessTokenResponse.getIdToken());
        oAuth2InMemoryRepository.put(email , accessTokenResponse);
        oAuth2Service.registerGmailWatchEvent(accessTokenResponse.getAccessToken());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/access-token/{email}")
    public ResponseEntity<String> getAccessToken(@PathVariable final String email) {
        AccessTokenResponse res = oAuth2InMemoryRepository.get(email);
        log.info("accessToken : {}", res.getAccessToken());
        return ResponseEntity.ok(res.getAccessToken());
    }

}
