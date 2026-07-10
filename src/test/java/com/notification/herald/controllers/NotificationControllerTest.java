package com.notification.herald.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean NotificationService notificationService;

  @Test
  void getNotification_validRequestId_returns200() throws Exception {
    ResponseDto serviceResponse = new ResponseDto("entity-data", HttpStatus.OK.value());
    when(notificationService.getNotification("req-123")).thenReturn(serviceResponse);

    mockMvc
        .perform(get("/notification").param("requestId", "req-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200));

    verify(notificationService).getNotification("req-123");
  }

  @Test
  void getNotification_missingRequestId_returns400() throws Exception {
    mockMvc.perform(get("/notification")).andExpect(status().isBadRequest());
  }
}
