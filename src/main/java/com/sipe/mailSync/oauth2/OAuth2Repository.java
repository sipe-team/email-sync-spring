package com.sipe.mailSync.oauth2;

import com.sipe.mailSync.oauth2.infra.GoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2Repository extends JpaRepository<GoogleToken, String> {

    Optional<GoogleToken> findByEmail(String email);
}
