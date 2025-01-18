package com.sipe.mailSync.kakao.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KakaoSelfSendRequest {

    @JsonProperty("gmail")
    private String gmail;
    @JsonProperty("message")
    private String message;

}
