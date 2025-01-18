package com.sipe.mailSync.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MeResponse(@NotEmpty String email, @NotNull(message = "kakaoStatus is empty") boolean kakaoStatus,
                         @NotNull(message = "gmailStatus is empty") boolean gmailStatus) {
}
