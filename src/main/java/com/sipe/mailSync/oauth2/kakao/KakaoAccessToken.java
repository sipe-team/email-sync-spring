package com.sipe.mailSync.oauth2.kakao;

import com.sipe.mailSync.oauth2.AccessTokenResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kakao_token")
public class KakaoAccessToken {
  @Id private String userId;
  private String accessToken;
  private String email;

  public static KakaoAccessToken from(String accessTokenResponse, String id, String email) {
    return KakaoAccessToken
            .builder()
            .accessToken(accessTokenResponse)
            .userId(id)
            .email(email).build();
  }
}
