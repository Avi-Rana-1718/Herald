package com.notification.herald.dto;

import jakarta.validation.constraints.Pattern;

public record UserDto(
    String name,
    String email
) {
}
