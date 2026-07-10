package com.notification.herald.dto;

import com.notification.herald.enums.NotifTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SMSTemplateTriggerDto extends CommonTemplateTriggerDto {

  @NotBlank(message = "toMobile can't be blank for SMS")
  private String toMobile;

  @Override
  public NotifTypeEnum getType() {
    return NotifTypeEnum.SMS;
  }
}
