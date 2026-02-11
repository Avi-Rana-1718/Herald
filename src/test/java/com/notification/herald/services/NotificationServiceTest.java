package com.notification.herald.services;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.UserDto;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.utils.RequestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyString;


@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private KafkaProviderService kafkaProviderService;

  @InjectMocks
  private NotificationService notificationService;

  private NotifRequestDto notifRequestDto;
  private List<UserDto> recipients;
  private String testRequestId;

  @BeforeEach
  void setUp() {
    testRequestId = "test-request-id-12345";
    recipients = List.of(
        new UserDto("John Doe", "john@example.com", "+1234567890"),
        new UserDto("Jane Smith", "jane@example.com", "+9876543210")
    );
  }

  @Test
  void sendNotification_withSingleNotificationType_shouldSendMessageToKafka() {
    notifRequestDto = new NotifRequestDto(
        List.of(NotifTypeEnum.EMAIL),
        recipients,
        "Test notification content",
        "Test Subject"
    );

    try (MockedStatic<RequestUtils> mockedRequestUtils = mockStatic(RequestUtils.class)) {
      mockedRequestUtils.when(RequestUtils::generateRequestId).thenReturn(testRequestId);

      ResponseDto response = notificationService.sendNotification(notifRequestDto);

      verify(kafkaProviderService, times(1)).sendMessage(eq("EMAIL"), any(EventDto.class));
      assertNotNull(response);
      assertEquals(testRequestId, response.data());
      assertEquals(HttpStatus.CREATED.value(), response.status());
    }
  }

  @Test
  void sendNotification_withMultipleNotificationTypes_shouldSendMultipleMessages() {
    notifRequestDto = new NotifRequestDto(
        List.of(NotifTypeEnum.EMAIL, NotifTypeEnum.SMS),
        recipients,
        "Test notification content",
        "Test Subject"
    );

    try (MockedStatic<RequestUtils> mockedRequestUtils = mockStatic(RequestUtils.class)) {
      mockedRequestUtils.when(RequestUtils::generateRequestId).thenReturn(testRequestId);

      ResponseDto response = notificationService.sendNotification(notifRequestDto);

      verify(kafkaProviderService, times(2)).sendMessage(anyString(), any(EventDto.class));
      verify(kafkaProviderService, times(1)).sendMessage(eq("EMAIL"), any(EventDto.class));
      verify(kafkaProviderService, times(1)).sendMessage(eq("SMS"), any(EventDto.class));
      assertNotNull(response);
      assertEquals(testRequestId, response.data());
      assertEquals(HttpStatus.CREATED.value(), response.status());
    }
  }

  @Test
  void sendNotification_shouldCreateEventDtoWithCorrectData() {
    notifRequestDto = new NotifRequestDto(
        List.of(NotifTypeEnum.EMAIL),
        recipients,
        "Test notification content",
        "Test Subject"
    );

    ArgumentCaptor<EventDto> eventDtoCaptor = ArgumentCaptor.forClass(EventDto.class);

    try (MockedStatic<RequestUtils> mockedRequestUtils = mockStatic(RequestUtils.class)) {
      mockedRequestUtils.when(RequestUtils::generateRequestId).thenReturn(testRequestId);

      notificationService.sendNotification(notifRequestDto);

      verify(kafkaProviderService).sendMessage(eq("EMAIL"), eventDtoCaptor.capture());
      EventDto capturedEventDto = eventDtoCaptor.getValue();

      assertNotNull(capturedEventDto);
      assertEquals(testRequestId, capturedEventDto.requestId());
      assertNotNull(capturedEventDto.user());
      assertEquals(recipients, capturedEventDto.recipients());
      assertEquals("Test notification content", capturedEventDto.content());
      assertEquals("Test Subject", capturedEventDto.subject());
    }
  }

  @Test
  void sendNotification_shouldGenerateUniqueUUIDForEachNotificationType() {
    notifRequestDto = new NotifRequestDto(
        List.of(NotifTypeEnum.EMAIL, NotifTypeEnum.SMS),
        recipients,
        "Test notification content",
        "Test Subject"
    );

    ArgumentCaptor<EventDto> eventDtoCaptor = ArgumentCaptor.forClass(EventDto.class);

    try (MockedStatic<RequestUtils> mockedRequestUtils = mockStatic(RequestUtils.class)) {
      mockedRequestUtils.when(RequestUtils::generateRequestId).thenReturn(testRequestId);

      notificationService.sendNotification(notifRequestDto);

      verify(kafkaProviderService, times(2)).sendMessage(anyString(), eventDtoCaptor.capture());
      List<EventDto> capturedEventDtos = eventDtoCaptor.getAllValues();

      assertEquals(2, capturedEventDtos.size());
      assertNotEquals(capturedEventDtos.get(0).user(), capturedEventDtos.get(1).user());
    }
  }

  @Test
  void sendNotification_withEmptyRecipients_shouldStillSendMessage() {
    notifRequestDto = new NotifRequestDto(
        List.of(NotifTypeEnum.EMAIL),
        List.of(),
        "Test notification content",
        "Test Subject"
    );

    try (MockedStatic<RequestUtils> mockedRequestUtils = mockStatic(RequestUtils.class)) {
      mockedRequestUtils.when(RequestUtils::generateRequestId).thenReturn(testRequestId);

      ResponseDto response = notificationService.sendNotification(notifRequestDto);

      verify(kafkaProviderService, times(1)).sendMessage(eq("EMAIL"), any(EventDto.class));
      assertNotNull(response);
      assertEquals(HttpStatus.CREATED.value(), response.status());
    }
  }

  @Test
  void sendNotification_shouldReturnCreatedStatus() {
    notifRequestDto = new NotifRequestDto(
        List.of(NotifTypeEnum.SMS),
        recipients,
        "Test notification content",
        "Test Subject"
    );

    try (MockedStatic<RequestUtils> mockedRequestUtils = mockStatic(RequestUtils.class)) {
      mockedRequestUtils.when(RequestUtils::generateRequestId).thenReturn(testRequestId);

      ResponseDto response = notificationService.sendNotification(notifRequestDto);

      assertEquals(201, response.status());
    }
  }

  @Test
  void sendNotification_shouldUseSameRequestIdForAllMessages() {
    notifRequestDto = new NotifRequestDto(
        List.of(NotifTypeEnum.EMAIL, NotifTypeEnum.SMS),
        recipients,
        "Test notification content",
        "Test Subject"
    );

    ArgumentCaptor<EventDto> eventDtoCaptor = ArgumentCaptor.forClass(EventDto.class);

    try (MockedStatic<RequestUtils> mockedRequestUtils = mockStatic(RequestUtils.class)) {
      mockedRequestUtils.when(RequestUtils::generateRequestId).thenReturn(testRequestId);

      notificationService.sendNotification(notifRequestDto);

      verify(kafkaProviderService, times(2)).sendMessage(anyString(), eventDtoCaptor.capture());
      List<EventDto> capturedEventDtos = eventDtoCaptor.getAllValues();

      assertEquals(testRequestId, capturedEventDtos.get(0).requestId());
      assertEquals(testRequestId, capturedEventDtos.get(1).requestId());
    }
  }
}
