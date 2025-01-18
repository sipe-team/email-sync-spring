package com.sipe.mailSync.oauth2;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OAuth2Controller {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);

    @Value("${app.client-id}")
    private String clientId;

    @Value("${app.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final OAuth2Service oAuth2Service;

    @GetMapping
    public ResponseEntity<String> oauth(@RequestParam String code) {
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
        oAuth2Service.saveGoogleToken(accessTokenResponse);
        oAuth2Service.registerGmailWatchEvent(accessTokenResponse.getAccessToken());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/access-token/{email}")
    public ResponseEntity<String> getAccessToken(@PathVariable final String email) {
        var accessToken =  oAuth2Service.getAccessTokenByEmail(email);
        log.info("accessToken : {}", accessToken);
        return ResponseEntity.ok(accessToken);
    }

}
