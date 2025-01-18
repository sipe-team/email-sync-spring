package com.sipe.mailSync.kakao.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class KakaoHeaderUtil {

    public HttpHeaders createKakaoHttpHeaders(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();

        // Set Content-Type
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set Authorization header
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        return httpHeaders;
    }
}

