package com.notification.herald.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SMSNotifRequestDto {
  String toMobile;
  String content;
}
