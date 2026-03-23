package com.notification.herald.controllers;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.otp.OtpRequestDto;
import com.notification.herald.dto.otp.OtpValidateDto;
import com.notification.herald.services.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("request")
    ResponseEntity<ResponseDto> requestOtp(@Valid @RequestBody OtpRequestDto requestDto) {
        ResponseDto responseDto = this.otpService.requestOtp(requestDto);
        return ResponseEntity.status(responseDto.status()).body(responseDto);
    }

    @PostMapping("/validate")
    ResponseEntity<ResponseDto> validateOtp(@Valid @RequestBody OtpValidateDto requestDto) {
        ResponseDto responseDto = this.otpService.validateOtp(requestDto);
        return ResponseEntity.status(responseDto.status()).body(responseDto);
    }
}
