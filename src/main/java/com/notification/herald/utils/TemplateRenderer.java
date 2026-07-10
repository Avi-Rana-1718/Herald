package com.notification.herald.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Substitutes {{placeholder}} tokens in template text with caller-supplied values. */
public final class TemplateRenderer {

  private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([\\w.]+)\\s*}}");

  private TemplateRenderer() {}

  public static String render(String template, Map<String, String> variables) {
    if (template == null) {
      return null;
    }
    Map<String, String> vars = variables == null ? Map.of() : variables;

    Matcher matcher = PLACEHOLDER.matcher(template);
    StringBuilder rendered = new StringBuilder();
    while (matcher.find()) {
      String key = matcher.group(1);
      String value = vars.get(key);
      if (value == null) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "missing value for template variable: " + key);
      }
      matcher.appendReplacement(rendered, Matcher.quoteReplacement(value));
    }
    matcher.appendTail(rendered);
    return rendered.toString();
  }
}
