package com.notification.herald.dto;

import com.notification.herald.enums.NotifTypeEnum;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommonTemplateCreationDto {

  @NotBlank(message = "templateName can't be blank")
  private String templateName;

  @NotBlank(message = "content can't be blank")
  private String content;

  @NotBlank(message = "langCode can't be blank")
  private String langCode;

  private List<String> variables;

  /** Channel this template belongs to. */
  public abstract NotifTypeEnum getType();

  /** Channel-specific fields persisted into the template's JSONB metadata. */
  public Map<String, Object> buildMetadata() {
    return new HashMap<>();
  }
}
