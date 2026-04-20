package com.notification.herald.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    NotificationService notificationService;

    @Test
    void postNotification_validRequest_returns201() throws Exception {
        NotifRequestDto dto = new NotifRequestDto(NotifTypeEnum.SMS, "+1234567890", null, null, "Hello", null);
        ResponseDto serviceResponse = new ResponseDto(List.of("req-123"), HttpStatus.CREATED.value());
        when(notificationService.sendNotification(anyList())).thenReturn(serviceResponse);

        mockMvc.perform(post("/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));

        verify(notificationService).sendNotification(anyList());
    }

    @Test
    void postNotification_nullType_returns400() throws Exception {
        String body = "[{\"type\":null,\"toMobile\":\"+1234567890\",\"content\":\"Hello\"}]";

        mockMvc.perform(post("/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postNotification_emptyList_returns201WithEmptyData() throws Exception {
        ResponseDto serviceResponse = new ResponseDto(List.of(), HttpStatus.CREATED.value());
        when(notificationService.sendNotification(anyList())).thenReturn(serviceResponse);

        mockMvc.perform(post("/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isCreated());
    }

    @Test
    void getNotification_validRequestId_returns200() throws Exception {
        ResponseDto serviceResponse = new ResponseDto("entity-data", HttpStatus.OK.value());
        when(notificationService.getNotification("req-123")).thenReturn(serviceResponse);

        mockMvc.perform(get("/notification").param("requestId", "req-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(notificationService).getNotification("req-123");
    }

    @Test
    void getNotification_missingRequestId_returns400() throws Exception {
        mockMvc.perform(get("/notification"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postNotification_serviceThrowsException_returns500() throws Exception {
        NotifRequestDto dto = new NotifRequestDto(NotifTypeEnum.SMS, "+1234567890", null, null, "Hello", null);
        when(notificationService.sendNotification(anyList())).thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(post("/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isInternalServerError());
    }
}
