package com.sipe.mailSync.oauth2;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class OAuth2InMemoryRepository {

    public final Map<String, AccessTokenResponse> map = new HashMap<>();

    public void put(String email, AccessTokenResponse accessTokenResponse) {
        map.put(email, accessTokenResponse);
    }

    public AccessTokenResponse get(String email) {
        return map.get(email);
    }

}
