package com.sipe.mailSync.kakao.service;

import com.sipe.mailSync.kakao.api.KakaoSelfSendRequest;
import com.sipe.mailSync.oauth2.OAuth2Service;
import com.sipe.mailSync.oauth2.kakao.KakaoAccessToken;
import com.sipe.mailSync.oauth2.kakao.KakaoAccessTokenRepository;
import com.sipe.mailSync.webclient.WebClientService;
import com.sipe.mailSync.constant.Properties;
import com.sipe.mailSync.kakao.util.KakaoSendHeaderUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
@Component
public class KakaoSelfMessageSender {

    @Resource
    private final Properties.KakaoApiConfigs kakaoApiConfigs;
    private final KakaoSendHeaderUtil kakaoSendHeaderUtil;
    private final WebClientService webClientService;
    private final OAuth2Service oAuth2Service;

    private final KakaoAccessTokenRepository KakaoAccessTokenRepository;

    public KakaoSelfMessageSender(KakaoSendHeaderUtil kakaoSendHeaderUtil, WebClientService webClientService, Properties.KakaoApiConfigs kakaoApiConfigs, OAuth2Service oAuth2Service, com.sipe.mailSync.oauth2.kakao.KakaoAccessTokenRepository kakaoAccessTokenRepository) {
        this.kakaoSendHeaderUtil = kakaoSendHeaderUtil;
        this.webClientService = webClientService;
        this.kakaoApiConfigs = kakaoApiConfigs;
        this.oAuth2Service = oAuth2Service;
        KakaoAccessTokenRepository = kakaoAccessTokenRepository;
    }

    public Mono<?> sendSelfMessage(KakaoSelfSendRequest kakaoSelfSendRequest) {
        String userId = oAuth2Service.getUserIdByEmail(kakaoSelfSendRequest.getGmail());
        KakaoAccessToken kakaoAccessToken = KakaoAccessTokenRepository.findById(userId).orElse(null);
        if (!ObjectUtils.isEmpty(kakaoAccessToken)) {
            // TODO userId를 토대로 ACCESS TOKEN 추출하여 아래 넣기
            KakaoSelfMessageRequest kakaoSelfMessageRequest =
                    KakaoSelfMessageRequest.builder()
                        .accessToken(kakaoAccessToken.getAccessToken())
                            .templateObject(
                                    KakaoSelfMessageRequest.TemplateObject.builder()
                                            .text(kakaoSelfSendRequest.getMessage())
                                            .build())
                            .build();
            return webClientService.sendPostReturnResponseEntity(
                            kakaoApiConfigs.getKAKAO_BASE_URL(),
                            kakaoApiConfigs.getKAKAO_SEND_ME_API_URI(),
                            kakaoSendHeaderUtil.createKakaoHttpHeaders(kakaoSelfMessageRequest.getAccessToken()),
                            convertRequestToMultiValueMap(kakaoSelfMessageRequest),
                            KakaoSelfMessageResponse.class
                    ).flatMap(responseEntity -> {
                        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                            return Mono.error(new RuntimeException("Error response: " + responseEntity.getStatusCode()));
                        }
                        return Mono.justOrEmpty(responseEntity.getBody());
                    })
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5))
                            .doBeforeRetry(retrySignal -> log.info("Retrying... attempt: {}, at: {}", retrySignal.totalRetries() + 1, LocalTime.now()))
                    )
                    .onErrorResume(throwable -> {
                        log.error("Error occurred while sending Kakao message: {}", throwable.getMessage(), throwable);
                        return Mono.error(throwable);
                    });
        }

        return Mono.empty();
    }

    private MultiValueMap<String, String> convertRequestToMultiValueMap(KakaoSelfMessageRequest kakaoSelfMessageRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("template_object", kakaoSelfMessageRequest.getTemplateObjectAsJson());
        return formData;
    }

}
