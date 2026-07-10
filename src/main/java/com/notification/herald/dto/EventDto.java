package com.notification.herald.dto;

public record EventDto(String requestId, UserDto recipients, String content, String subject) {}
