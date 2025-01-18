package com.sipe.mailSync.auth.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

public record RegisterResponse(@NotEmpty(message = "created is empty") LocalDateTime created) {
}
