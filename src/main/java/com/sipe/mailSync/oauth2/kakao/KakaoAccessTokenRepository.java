package com.sipe.mailSync.oauth2.kakao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoAccessTokenRepository extends JpaRepository<KakaoAccessToken, String> {

}
