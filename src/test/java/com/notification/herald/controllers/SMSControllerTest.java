package com.notification.herald.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSNotifRequestDto;
import com.notification.herald.services.SMSNotificationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SMSController.class)
class SMSControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean SMSNotificationService smsNotificationService;

  @Test
  void postSms_validRequest_returns201() throws Exception {
    SMSNotifRequestDto dto = new SMSNotifRequestDto("+1234567890", "Hello");
    ResponseDto serviceResponse = new ResponseDto(List.of("req-123"), HttpStatus.CREATED.value());
    when(smsNotificationService.sendSms(any())).thenReturn(serviceResponse);

    mockMvc
        .perform(
            post("/notification/sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(201));

    verify(smsNotificationService).sendSms(any());
  }

  @Test
  void postSms_serviceThrowsException_returns500() throws Exception {
    SMSNotifRequestDto dto = new SMSNotifRequestDto("+1234567890", "Hello");
    when(smsNotificationService.sendSms(any())).thenThrow(new RuntimeException("unexpected"));

    mockMvc
        .perform(
            post("/notification/sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isInternalServerError());
  }
}
