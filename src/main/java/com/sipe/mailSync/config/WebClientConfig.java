package com.sipe.mailSync.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipe.mailSync.constant.Properties;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Slf4j
@Configuration
public class WebClientConfig {

    @Resource private final Properties.WebClientConfigs webClientConfigs;

    public WebClientConfig(Properties.WebClientConfigs webClientConfigs) {
        this.webClientConfigs = webClientConfigs;
    }


    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(makeHttpClient()))
                .exchangeStrategies(makeExchangeStrategies())
                .filters(customFilter -> {
                    customFilter.add(makeRequestCustomFilter());
                    customFilter.add(makeResponseCustomFilter());
                })
                .build();
    }


    private HttpClient makeHttpClient() {
        return HttpClient.create(makeConnectionProvider())
                .secure(sslContextSpec -> {
                    try {
                        sslContextSpec.sslContext(SslContextBuilder
                                .forClient()
                                // SSL 인증서 확인을 비활성화 => 즉, 클라이언트는 유효하지 않거나 신뢰할 수 없는 인증서라도 모든 SSL 인증서를 수락
                                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                .build());
                    } catch (SSLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientConfigs.getCONNECTION_TIMEOUT_MILLISECOND())
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(webClientConfigs.getREAD_TIMEOUT_SECOND()))
                                .addHandlerLast(new WriteTimeoutHandler(webClientConfigs.getWRITE_TIMEOUT_SECOND()))
                );
    }


    private ConnectionProvider makeConnectionProvider(){
        return ConnectionProvider.builder(webClientConfigs.getCONNECTION_PROVIDER_NAME())
                .maxConnections(webClientConfigs.getCONNECTION_PROVIDER_MAX_CONNECTIONS())
                .pendingAcquireMaxCount(webClientConfigs.getCONNECTION_PROVIDER_PENDING_ACQUIRE_MAX_COUNT())
                .pendingAcquireTimeout(Duration.ofSeconds(webClientConfigs.getCONNECTION_PROVIDER_PENDING_ACQUIRE_TIMEOUT_SECOND()))
                .build();
    }


    private ExchangeStrategies makeExchangeStrategies(){
        return ExchangeStrategies.builder()
                .codecs(configurer -> {
                            configurer.defaultCodecs().maxInMemorySize(webClientConfigs.getPROVIDER_MAX_IN_MEMORY_SIZE_OF_BYTE());
                            configurer.customCodecs().register(new Jackson2JsonDecoder(new ObjectMapper(), MediaType.TEXT_PLAIN));
                        }
                )
                .build();
    }


    private ExchangeFilterFunction makeRequestCustomFilter(){
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("[REQUEST] => URL : {} / method : {} / Headers : {}", clientRequest.url(), clientRequest.method(), clientRequest.headers());

            return Mono.just(clientRequest);
        });
    }


    private ExchangeFilterFunction makeResponseCustomFilter(){
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("[RESPONSE] <= Headers : {}", clientResponse.headers().asHttpHeaders());
            return Mono.just(clientResponse);
        });
    }

}
