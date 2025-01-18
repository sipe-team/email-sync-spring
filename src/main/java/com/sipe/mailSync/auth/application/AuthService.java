package com.sipe.mailSync.auth.application;

import com.sipe.mailSync.auth.dto.MeResponse;
import com.sipe.mailSync.auth.dto.RegisterRequest;
import com.sipe.mailSync.auth.dto.RegisterResponse;
import com.sipe.mailSync.oauth2.OAuth2Repository;
import com.sipe.mailSync.security.dto.CustomUserDetail;
import com.sipe.mailSync.user.domain.User;
import com.sipe.mailSync.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OAuth2Repository oAuth2Repository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterResponse register(RegisterRequest body) {
        String email = body.email();
        if(userRepository.existsByEmail(email)){
            throw new DuplicateKeyException("This email Already Used");
        }

        User user = body.toUser(bCryptPasswordEncoder);
        User savedUser = userRepository.save(user);
        return new RegisterResponse(savedUser.getCreatedAt());
    }

    @Transactional
    public MeResponse getStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
        String email = userDetails.getEmail();
        boolean googleStatus = oAuth2Repository.existsByEmail(userDetails.getEmail());


        return new MeResponse(email,false,googleStatus);
    }
}
