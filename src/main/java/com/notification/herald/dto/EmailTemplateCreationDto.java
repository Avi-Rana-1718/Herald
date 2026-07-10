package com.notification.herald.dto;

import com.notification.herald.enums.NotifTypeEnum;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplateCreationDto extends CommonTemplateCreationDto {

  @NotBlank(message = "title can't be blank for email")
  private String title;

  @Override
  public NotifTypeEnum getType() {
    return NotifTypeEnum.EMAIL;
  }

  @Override
  public Map<String, Object> buildMetadata() {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("title", title);
    return metadata;
  }
}
