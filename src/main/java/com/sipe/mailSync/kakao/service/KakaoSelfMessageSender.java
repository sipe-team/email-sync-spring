package com.sipe.mailSync.kakao.service;

import com.sipe.mailSync.webclient.WebClientService;
import com.sipe.mailSync.constant.Properties;
import com.sipe.mailSync.kakao.util.KakaoHeaderUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
@Component
public class KakaoSelfMessageSender {

    @Resource
    private final Properties.KakaoApiConfigs kakaoApiConfigs;
    private final KakaoHeaderUtil kakaoHeaderUtil;
    private final WebClientService webClientService;

    public KakaoSelfMessageSender(KakaoHeaderUtil kakaoHeaderUtil, WebClientService webClientService, Properties.KakaoApiConfigs kakaoApiConfigs) {
        this.kakaoHeaderUtil = kakaoHeaderUtil;
        this.webClientService = webClientService;
        this.kakaoApiConfigs = kakaoApiConfigs;
    }

    public Mono<?> sendSelfMessage(KakaoSelfMessageRequest kakaoSelfMessageRequest) {
        return webClientService.sendPostReturnResponseEntity(
                        kakaoApiConfigs.getKAKAO_BASE_URL(),
                        kakaoApiConfigs.getKAKAO_SEND_ME_API_URI(),
                        kakaoHeaderUtil.createKakaoHttpHeaders(kakaoSelfMessageRequest.getAccessToken()),
                        convertRequestToMultiValueMap(kakaoSelfMessageRequest),
                        KakaoSelfMessageResponse.class
                ).flatMap(responseEntity -> {
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        return Mono.error(new RuntimeException("Error response: " + responseEntity.getStatusCode()));
                    }
                    return Mono.justOrEmpty(responseEntity.getBody());
                })
                .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(2))
                        .doBeforeRetry(retrySignal -> log.info("Retrying... attempt: {}, at: {}", retrySignal.totalRetries() + 1, LocalTime.now()))
                )
                .onErrorResume(throwable -> {
                    log.error("Error occurred while sending Kakao message: {}", throwable.getMessage(), throwable);
                    return Mono.error(throwable);
                });
    }

    private MultiValueMap<String, String> convertRequestToMultiValueMap(KakaoSelfMessageRequest kakaoSelfMessageRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("template_object", kakaoSelfMessageRequest.getTemplateObjectAsJson());
        return formData;
    }

}
