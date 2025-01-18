package com.sipe.mailSync.kakao.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KakaoSelfMessageRequest {

    private String accessToken;
    @JsonProperty("template_object")
    private TemplateObject templateObject;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class TemplateObject {

        @JsonProperty("object_type")
        private final String objectType = "text";

        @JsonProperty("text")
        private String text;

        @JsonProperty("link")
        private final LinkDetail link = new LinkDetail();

        @JsonProperty("button_title")
        private final String buttonTitle = "메일 확인하기";
    }

    @Getter
    @ToString
    public static class LinkDetail {

        @JsonProperty("web_url")
        private final String webUrl = "https://mail.google.com/mail/u/0/?ogbl#inbox";

        @JsonProperty("mobile_web_url")
        private final String mobileWebUrl = "https://mail.google.com/mail/u/0/?ogbl#inbox";

    }

    public String getTemplateObjectAsJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this.templateObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert templateObject to JSON", e);
        }
    }

}
