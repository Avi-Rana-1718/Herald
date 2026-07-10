package com.notification.herald.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.herald.dto.EmailNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.services.EmailNotificationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean EmailNotificationService emailNotificationService;

  @Test
  void postEmail_validRequest_returns201() throws Exception {
    EmailNotifRequestDto dto =
        new EmailNotifRequestDto("user@example.com", "Alice", "Subject", "Hello");
    ResponseDto serviceResponse = new ResponseDto(List.of("req-123"), HttpStatus.CREATED.value());
    when(emailNotificationService.sendEmail(any())).thenReturn(serviceResponse);

    mockMvc
        .perform(
            post("/notification/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(201));

    verify(emailNotificationService).sendEmail(any());
  }

  @Test
  void postEmail_serviceThrowsException_returns500() throws Exception {
    EmailNotifRequestDto dto =
        new EmailNotifRequestDto("user@example.com", "Alice", "Subject", "Hello");
    when(emailNotificationService.sendEmail(any())).thenThrow(new RuntimeException("unexpected"));

    mockMvc
        .perform(
            post("/notification/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isInternalServerError());
  }
}
