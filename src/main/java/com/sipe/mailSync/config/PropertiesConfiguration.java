package com.sipe.mailSync.config;

import com.sipe.mailSync.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {JwtProperties.class}) //추가되는 Properties 객체가 있는 경우 배열에 추가로 명시
public class PropertiesConfiguration {
}
