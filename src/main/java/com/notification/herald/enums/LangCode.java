package com.notification.herald.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LangCode {
  EN("en", "English"),
  ES("es", "Spanish"),
  FR("fr", "French"),
  DE("de", "German"),
  PT("pt", "Portuguese"),
  HI("hi", "Hindi"),
  ZH("zh", "Chinese"),
  JA("ja", "Japanese");

  private final String code;
  private final String displayName;

  public static boolean validate(String code) {
    for (LangCode langCode : values()) {
      if (langCode.code.equalsIgnoreCase(code)) {
        return true;
      }
    }

    return false;
  }
}
