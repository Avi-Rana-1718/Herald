package com.notification.herald.dto;

import com.notification.herald.enums.NotifTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplateTriggerDto extends CommonTemplateTriggerDto {

  @NotBlank(message = "toEmail can't be blank for email")
  private String toEmail;

  private String toName;

  @Override
  public NotifTypeEnum getType() {
    return NotifTypeEnum.EMAIL;
  }
}
