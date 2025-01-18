package com.sipe.mailSync.oauth2.kakao;

import com.sipe.mailSync.oauth2.AccessTokenResponse;
import com.sipe.mailSync.user.infra.UserRepository;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("/oauth2/kakao")
@RestController
@RequiredArgsConstructor
public class OAuth2KakaoController {
  private static final Logger log = LoggerFactory.getLogger(OAuth2KakaoController.class);
  private final RestTemplate restTemplate;
  private final KakaoAccessTokenRepository kakaoAccessTokenRepository;
  private final UserRepository userRepository;

  @Value("${app.kakao.client-id}")
  private String clientId;

  @Value("${app.kakao.client-secret}")
  private String clientSecret;

  @Value("${app.front-url}")
  private String frontUrl;

  @GetMapping
  public ResponseEntity<String> test(
      @RequestParam("code") String code, @RequestParam("state") String scope) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/x-www-form-urlencoded");

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
    String accessToken = accessTokenResponse.getAccessToken();
    log.info(accessToken);

   MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");

    HttpHeaders infoHeader = new HttpHeaders();
    headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
    headers.add("Authorization", "Bearer " + accessToken);
    HttpEntity<MultiValueMap<String, String>> requestInfoMap =
        new HttpEntity<>(body, infoHeader);

    ResponseEntity<String> responseInfo =
        restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me", HttpMethod.POST, requestInfoMap, String.class);

    log.info(responseInfo.toString());

    //    Optional<User> byId = userRepository.findById(state);
    //    kakaoAccessTokenRepository.save(
    //        KakaoAccessToken.from(accessTokenResponse.getAccessToken(), "user.getId()",
    // account_email));
    //    if (byId.isPresent()) {
    //      User user = byId.get();
    //      kakaoAccessTokenRepository.save(
    //          KakaoAccessToken.from(
    //              accessTokenResponse.getAccessToken(), user.getId(), user.getEmail()));
    //    } else {
    //      throw new IllegalArgumentException("no user exist");
    //    }

    return ResponseEntity.ok().body("");
  }

  @GetMapping("/user/me")
  public RedirectView info(String data) {
    log.info(data);
    return new RedirectView(frontUrl + "?kakaoOAuthSuccess=ture");
  }
}
