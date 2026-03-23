package com.notification.herald.dto.otp;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpValidateDto {
    @NotBlank(message = "RequestId is mandatory")
    String requestId;
    @NotBlank(message = "OTP is mandatory")
    String otp;
}
