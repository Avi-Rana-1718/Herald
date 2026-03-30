package com.notification.herald.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.otp.OtpRequestDto;
import com.notification.herald.dto.otp.OtpValidateDto;
import com.notification.herald.services.OtpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OtpController.class)
class OtpControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OtpService otpService;

    // --- /otp/request ---

    @Test
    void requestOtp_validRequest_returnsServiceStatus() throws Exception {
        OtpRequestDto dto = new OtpRequestDto("+1234567890", null, "Your OTP", "Alice", 300);
        ResponseDto serviceResponse = new ResponseDto("otp-req-id", HttpStatus.OK.value());
        when(otpService.requestOtp(any())).thenReturn(serviceResponse);

        mockMvc.perform(post("/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(otpService).requestOtp(any());
    }

    @Test
    void requestOtp_blankContent_returns400() throws Exception {
        OtpRequestDto dto = new OtpRequestDto("+1234567890", null, "", "Alice", 300);

        mockMvc.perform(post("/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestOtp_nullExpiresIn_returns400() throws Exception {
        String body = "{\"toMobile\":\"+1234567890\",\"content\":\"OTP\",\"expiresIn\":null}";

        mockMvc.perform(post("/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // --- /otp/validate ---

    @Test
    void validateOtp_validRequest_returnsServiceStatus() throws Exception {
        OtpValidateDto dto = new OtpValidateDto();
        dto.setRequestId("req-123");
        dto.setOtp("12345");
        ResponseDto serviceResponse = new ResponseDto("validated", HttpStatus.OK.value());
        when(otpService.validateOtp(any())).thenReturn(serviceResponse);

        mockMvc.perform(post("/otp/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(otpService).validateOtp(any());
    }

    @Test
    void validateOtp_blankRequestId_returns400() throws Exception {
        OtpValidateDto dto = new OtpValidateDto();
        dto.setRequestId("");
        dto.setOtp("12345");

        mockMvc.perform(post("/otp/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateOtp_blankOtp_returns400() throws Exception {
        OtpValidateDto dto = new OtpValidateDto();
        dto.setRequestId("req-123");
        dto.setOtp("");

        mockMvc.perform(post("/otp/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
