package com.notification.herald.services;

import com.notification.herald.dto.CommonTemplateCreationDto;
import com.notification.herald.dto.EmailNotifRequestDto;
import com.notification.herald.dto.EmailTemplateTriggerDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSNotifRequestDto;
import com.notification.herald.dto.SMSTemplateTriggerDto;
import com.notification.herald.entities.TemplateEntity;
import com.notification.herald.enums.LangCode;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.repository.TemplateRepository;
import com.notification.herald.utils.TemplateRenderer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TemplateService {

  private final TemplateRepository templateRepository;
  private final EmailNotificationService emailNotificationService;
  private final SMSNotificationService smsNotificationService;

  public ResponseDto createTemplate(CommonTemplateCreationDto request) {
    validateLangCode(request.getLangCode());

    NotifTypeEnum type = request.getType();
    if (templateRepository.existsByTemplateNameAndTypeAndLangCode(
        request.getTemplateName(), type, request.getLangCode())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "template already exists for name="
              + request.getTemplateName()
              + ", type="
              + type
              + ", langCode="
              + request.getLangCode());
    }

    TemplateEntity entity = new TemplateEntity();
    entity.setTemplateName(request.getTemplateName());
    entity.setContent(request.getContent());
    entity.setLangCode(request.getLangCode());
    entity.setType(type);
    entity.setVariables(request.getVariables());
    entity.setMetadata(request.buildMetadata());
    entity.setVersion(1);
    entity.setIsActive(true);

    TemplateEntity saved = templateRepository.save(entity);

    return new ResponseDto(List.of(saved.getTemplateId().toString()), HttpStatus.CREATED.value());
  }

  public ResponseDto triggerEmailTemplate(EmailTemplateTriggerDto request) {
    TemplateEntity template =
        getActiveTemplate(request.getTemplateName(), NotifTypeEnum.EMAIL, request.getLangCode());

    String content = TemplateRenderer.render(template.getContent(), request.getVariables());
    String title =
        template.getMetadata() == null ? null : (String) template.getMetadata().get("title");
    String subject = TemplateRenderer.render(title, request.getVariables());

    EmailNotifRequestDto emailRequest =
        new EmailNotifRequestDto(request.getToEmail(), request.getToName(), subject, content);
    return emailNotificationService.sendEmail(emailRequest);
  }

  public ResponseDto triggerSmsTemplate(SMSTemplateTriggerDto request) {
    TemplateEntity template =
        getActiveTemplate(request.getTemplateName(), NotifTypeEnum.SMS, request.getLangCode());

    String content = TemplateRenderer.render(template.getContent(), request.getVariables());

    SMSNotifRequestDto smsRequest = new SMSNotifRequestDto(request.getToMobile(), content);
    return smsNotificationService.sendSms(smsRequest);
  }

  private TemplateEntity getActiveTemplate(String name, NotifTypeEnum type, String langCode) {
    TemplateEntity template =
        templateRepository
            .findByTemplateNameAndTypeAndLangCode(name, type, langCode)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "no template found for name="
                            + name
                            + ", type="
                            + type
                            + ", langCode="
                            + langCode));

    if (Boolean.FALSE.equals(template.getIsActive())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "template is inactive: " + name);
    }
    return template;
  }

  private void validateLangCode(String langCode) {
    if (!LangCode.validate(langCode)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "unsupported langCode: " + langCode);
    }
  }
}
