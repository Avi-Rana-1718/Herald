package com.notification.herald.dto;


import com.notification.herald.enums.NotifTypeEnum;

import java.util.List;

public record NotifRequestDto(
    List<NotifTypeEnum> type,
    List<UserDto> recipients,
    String content,
    String subject
) {}
