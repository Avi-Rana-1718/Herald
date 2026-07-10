package com.notification.herald.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailNotifRequestDto {
  String toEmail;
  String toName;
  String subject;
  String content;
}
