package com.sipe.mailSync.kakao.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KakaoSelfSendRequest {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("message")
    private String message;

}
