package com.notification.herald.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InAppNotifRequestDto {
  @NotBlank String uuid;
  @NotBlank String title;
  @NotBlank String content;
}
