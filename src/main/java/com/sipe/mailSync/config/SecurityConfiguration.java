package com.sipe.mailSync.config;

import com.sipe.mailSync.security.exceptions.RestAuthenticationEntryPoint;
import com.sipe.mailSync.security.exceptions.TokenAccessDeniedHandler;
import com.sipe.mailSync.security.filter.EmailPasswordAuthenticationFilter;
import com.sipe.mailSync.security.filter.JwtAuthenticationFilter;
import com.sipe.mailSync.security.jwt.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtTokenManager jwtTokenManager;
  private final TokenAccessDeniedHandler tokenAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성
        .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 비활성
        .addFilterBefore(
            jwtAuthenticationFilter, EmailPasswordAuthenticationFilter.class) // jwt 인증 필터
        .addFilterAt(
            new EmailPasswordAuthenticationFilter(
                authenticationManager(authenticationConfiguration), jwtTokenManager),
            UsernamePasswordAuthenticationFilter.class) // email - password 로그인 필터
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // 인증 진입점 설정
                    .accessDeniedHandler(tokenAccessDeniedHandler)) // 예외 처리 설정
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS)) // 세션 비활성 (HTTP 요청마다 인증 필요)
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers("/")
                    .permitAll() // health check
                    .requestMatchers("/api/auth/*")
                    .permitAll()
                    .requestMatchers(
                        "/swagger-ui/*",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v2/**",
                        "/h2/**",
                        "/oauth2/kakao",
                        "/oauth2/kakao/token",
                        "/v3/**",
                        "/swagger-resources/**",
                        "/error")
                    .permitAll() // swagger 접근 허용
                    .requestMatchers("/favicon.ico")
                    .permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
