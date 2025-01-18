package com.sipe.mailSync.auth.application;

import com.sipe.mailSync.auth.dto.RegisterRequest;
import com.sipe.mailSync.auth.dto.RegisterResponse;
import com.sipe.mailSync.user.domain.User;
import com.sipe.mailSync.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
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
}
