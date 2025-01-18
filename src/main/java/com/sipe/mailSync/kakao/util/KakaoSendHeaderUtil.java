package com.sipe.mailSync.kakao.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class KakaoSendHeaderUtil {

    public HttpHeaders createKakaoHttpHeaders(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        return httpHeaders;
    }
}

