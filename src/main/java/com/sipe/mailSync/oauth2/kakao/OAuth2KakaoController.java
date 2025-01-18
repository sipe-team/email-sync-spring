package com.sipe.mailSync.oauth2.kakao;

import com.sipe.mailSync.oauth2.AccessTokenResponse;
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

@RequestMapping("/oauth2/kakao")
@RestController
@RequiredArgsConstructor
public class OAuth2KakaoController {
  private static final Logger log = LoggerFactory.getLogger(OAuth2KakaoController.class);
  private final RestTemplate restTemplate;

  @Value("${app.kakao.client-id}")
  private String clientId;

  @Value("${app.kakao.client-secret}")
  private String clientSecret;

  @GetMapping
  public ResponseEntity<String> test(@RequestParam("code") String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/x-www-form-urlencoded");

    log.info(code);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("code", code);
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    map.add("redirect_uri", "http://localhost:8000/oauth2/kakao");
    map.add("grant_type", "authorization_code");
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    ResponseEntity<AccessTokenResponse> response =
        restTemplate.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            request,
            AccessTokenResponse.class);

    AccessTokenResponse accessTokenResponse = response.getBody();
    log.info("access token" + accessTokenResponse.getAccessToken());
    return ResponseEntity.ok("success");
  }
}
