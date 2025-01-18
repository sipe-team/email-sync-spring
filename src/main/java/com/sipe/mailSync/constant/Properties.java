package com.sipe.mailSync.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {

    @Getter
    @Component
    public static class WebClientConfigs {
        @Value("${webclient.connection.timeout.millisecond}")
        private int CONNECTION_TIMEOUT_MILLISECOND;

        @Value("${webclient.read.timeout.second}")
        private int READ_TIMEOUT_SECOND;

        @Value("${webclient.write.timeout.second}")
        private int WRITE_TIMEOUT_SECOND;

        @Value("${webclient.connection.provider.name}")
        private String CONNECTION_PROVIDER_NAME;

        @Value("${webclient.connection.provider.max.connections}")
        private int CONNECTION_PROVIDER_MAX_CONNECTIONS;

        @Value("${webclient.connection.provider.pending.acquire.max.count}")
        private int CONNECTION_PROVIDER_PENDING_ACQUIRE_MAX_COUNT;

        @Value("${webclient.connection.provider.pending.acquire.timeout.second}")
        private int CONNECTION_PROVIDER_PENDING_ACQUIRE_TIMEOUT_SECOND;

        @Value("${webclient.provider.max.in.meroty.size.of.byte}")
        private int PROVIDER_MAX_IN_MEMORY_SIZE_OF_BYTE;

        @Value("${webclient.block.timeout.of.millis}")
        private int WEBCLIENT_BLOCK_TIMEOUT_OF_MILLIS;
    }

    @Getter
    @Component
    public static class KakaoApiConfigs {

        private String KAKAO_BASE_URL = "https://kapi.kakao.com/v2/api";
        private String KAKAO_SEND_ME_API_URI = "/talk/memo/default/send";
        private String KAKAO_SEND_ME_API_OBJECT_TYPE_HEADER_VALUE = "object_type";
        private String KAKAO_SEND_ME_API_TEXT_HEADER_VALUE = "text";

    }

}
