package com.notification.herald.dto;

import jakarta.validation.constraints.Pattern;

public record UserDto(
    String name,
    String email,
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$",
    message = "Phone number must be in E.164 format")
    String phoneNumber
) {
}
