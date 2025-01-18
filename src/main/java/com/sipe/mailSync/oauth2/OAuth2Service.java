package com.sipe.mailSync.oauth2;

import com.sipe.mailSync.oauth2.infra.GoogleToken;
import com.sipe.mailSync.user.domain.User;
import com.sipe.mailSync.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class OAuth2Service {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Service.class);

    private final RestTemplate restTemplate;
    private final OAuth2Repository oAuth2Repository;
    private final UserRepository userRepository;

    @Value("${app.topic}")
    private String topic;

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

        log.info("발행성공 {}", response);
    }

    public void saveGoogleToken(AccessTokenResponse accessTokenResponse) {
        String email = getEmail(accessTokenResponse.getIdToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("email not found"));

        Optional<GoogleToken> googleToken = oAuth2Repository.findByEmail(email);
        if (googleToken.isPresent()) {
            GoogleToken token = googleToken.get();
            token.updateAccessToken(accessTokenResponse.getAccessToken());
        } else {
            oAuth2Repository.save(accessTokenResponse.toEntity(email, user.getId()));
        }
    }

    public String getAccessTokenByEmail(String email) {
        var response = oAuth2Repository.findByEmail(email).orElseThrow(() -> new RuntimeException("email not found"));
        return response.getAccessToken();
    }

    public String getUserIdByEmail(String email) {
        var response = oAuth2Repository.findByEmail(email).orElseThrow(() -> new RuntimeException("email not found"));
        return response.getUserId();
    }
}
