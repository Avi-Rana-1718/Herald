package com.notification.herald.dto;


import com.notification.herald.enums.NotifTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotifRequestDto{
    @NotNull(message = "type is mandatory")
    NotifTypeEnum type;
    String toMobile;
    String toEmail;
    String toName;
    String content;
    String subject;
}
