package com.sipe.mailSync.oauth2.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleToken {
    @Id
    @GeneratedValue
    private long id;

    private String email;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    @Transactional
    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
