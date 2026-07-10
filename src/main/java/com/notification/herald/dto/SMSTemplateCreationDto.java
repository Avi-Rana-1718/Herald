package com.notification.herald.dto;

import com.notification.herald.enums.NotifTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SMSTemplateCreationDto extends CommonTemplateCreationDto {

  @Override
  public NotifTypeEnum getType() {
    return NotifTypeEnum.SMS;
  }
}
