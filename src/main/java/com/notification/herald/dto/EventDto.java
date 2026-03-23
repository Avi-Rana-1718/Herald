package com.notification.herald.dto;

import java.util.List;
import java.util.UUID;

public record EventDto (
    String requestId,
    UserDto recipients,
    String content,
    String subject
) {}
