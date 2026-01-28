package com.notification.herald.dto;

import java.util.UUID;

public record EventDto (
    UUID requestId,
    UUID userId,
    String type,
    Object payload
) {}
