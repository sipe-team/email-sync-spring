package com.sipe.mailSync.kakao.api;

import com.sipe.mailSync.kakao.service.KakaoSelfMessageRequest;
import com.sipe.mailSync.kakao.service.KakaoSelfMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/kakao")
public class KakaoSendApi {

    private final KakaoSelfMessageSender kakaoSelfMessageSender;

    public KakaoSendApi(KakaoSelfMessageSender kakaoSelfMessageSender) {
        this.kakaoSelfMessageSender = kakaoSelfMessageSender;
    }

    @GetMapping(value = "/self/send/test")
    public ResponseEntity<?> test() {

        kakaoSelfMessageSender.sendSelfMessage(
                        KakaoSelfMessageRequest.builder()
                                .accessToken("aOXov1fiRrGy1140_iLNemIXmPq3vrFyAAAAAQo8JFkAAAGUd9yTl-jqOP6o1CZo")
                                .templateObject(
                                        KakaoSelfMessageRequest.TemplateObject.builder()
                                                .text("TEST")
                                                .build())
                                .build())
                .subscribe(response -> log.info("Message sent successfully: {}", response),
                        error -> log.error("Final error after retries: {}", error.getMessage(), error));

        return ResponseEntity.ok("TEST");
    }

    @GetMapping(value = "/self/send")
    public ResponseEntity<?> sendKakao(@RequestBody KakaoSelfSendRequest kakaoSelfSendRequest) {

        kakaoSelfMessageSender.sendSelfMessage(
                KakaoSelfMessageRequest.builder()
                        // TODO 엑세스 토큰 연동
//                            .accessToken(kakaoSelfSendRequest.getAccessToken())
                            .templateObject(
                                    KakaoSelfMessageRequest.TemplateObject.builder()
                                            .text(kakaoSelfSendRequest.getMessage())
                                            .build())
                        .build())
                .subscribe(response -> log.info("Message sent successfully: {}", response),
                        error -> log.error("Final error after retries: {}", error.getMessage(), error));

        return ResponseEntity.ok("START");
    }
}
