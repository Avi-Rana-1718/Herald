package com.notification.herald.dto;

import com.notification.herald.enums.NotifTypeEnum;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommonTemplateTriggerDto {

  @NotBlank(message = "templateName can't be blank")
  private String templateName;

  @NotBlank(message = "langCode can't be blank")
  private String langCode;

  /** Values substituted into the template's {{placeholders}} at send time. */
  private Map<String, String> variables;

  /** Channel of the template to resolve and send. */
  public abstract NotifTypeEnum getType();
}
