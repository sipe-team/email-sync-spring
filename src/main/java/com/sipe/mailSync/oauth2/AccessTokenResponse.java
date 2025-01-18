package com.sipe.mailSync.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sipe.mailSync.oauth2.infra.GoogleToken;
import lombok.Data;

@Data
public class AccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expired_in")
    private Integer expiredIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("id_token")
    private String idToken;

    public GoogleToken toEntity(String email,String userId){
        return GoogleToken.builder()
                .userId(userId)
                .email(email)
                .accessToken(this.accessToken)
                .refreshToken(this.refreshToken)
                .build();
    }

}
