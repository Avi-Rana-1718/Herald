package com.notification.herald.dto;

public record ErrorDto (
    String message,
    Boolean success
) {}
