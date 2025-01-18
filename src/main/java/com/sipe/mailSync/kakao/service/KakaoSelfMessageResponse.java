package com.sipe.mailSync.kakao.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KakaoSelfMessageResponse {

    @JsonProperty("result_code")
    private String  resultCode;
}
