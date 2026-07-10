package com.notification.herald.services;

import com.notification.herald.dto.EmailNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSNotifRequestDto;
import com.notification.herald.dto.otp.OtpRequestDto;
import com.notification.herald.dto.otp.OtpValidateDto;
import com.notification.herald.utils.RequestUtils;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OtpService {

  private final EmailNotificationService emailNotificationService;
  private final SMSNotificationService smsNotificationService;
  private final SecureRandom secureRandom = new SecureRandom();
  private final int OTP_LENGTH = 5;
  private final RedisTemplate<String, String> redisTemplate;

  public ResponseDto requestOtp(OtpRequestDto requestDto) {
    if (Objects.isNull(requestDto.email()) && Objects.isNull(requestDto.sms())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "at least one of email or sms must be provided");
    }

    String otp = this.generateOtp();
    String content = requestDto.content().replace("${OTP}", otp);
    String otpId = RequestUtils.generateRequestId();

    // One code, hashed once, stored under a single OTP id — validated once regardless of channels.
    String hashedOtp = BCrypt.hashpw(otp, BCrypt.gensalt(5));
    redisTemplate
        .opsForValue()
        .set("otp:" + otpId, hashedOtp, Duration.ofSeconds(requestDto.expiresIn()));

    if (Objects.nonNull(requestDto.email())) {
      EmailNotifRequestDto emailRequest =
          new EmailNotifRequestDto(
              requestDto.email().toEmail(),
              requestDto.email().recipientName(),
              "OTP request",
              content);
      emailNotificationService.sendEmail(emailRequest);
    }

    if (Objects.nonNull(requestDto.sms())) {
      SMSNotifRequestDto smsRequest = new SMSNotifRequestDto(requestDto.sms().toMobile(), content);
      smsNotificationService.sendSms(smsRequest);
    }

    return new ResponseDto(otpId, HttpStatus.OK.value());
  }

  public ResponseDto validateOtp(OtpValidateDto requestDto) {
    String hashedOtp = redisTemplate.opsForValue().get("otp:" + requestDto.getRequestId());

    if (Objects.isNull(hashedOtp)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No OTP found for this requestId");
    }

    if (!BCrypt.checkpw(requestDto.getOtp(), hashedOtp)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP provided");
    }

    redisTemplate.delete("otp:" + requestDto.getRequestId());

    return new ResponseDto("Authorized", HttpStatus.OK.value());
  }

  private String generateOtp() {
    StringBuilder otp = new StringBuilder(OTP_LENGTH);

    for (int i = 0; i < OTP_LENGTH; i++) {
      otp.append(secureRandom.nextInt(10));
    }

    return otp.toString();
  }
}
