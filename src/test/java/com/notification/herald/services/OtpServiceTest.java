package com.notification.herald.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.notification.herald.dto.EmailNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSNotifRequestDto;
import com.notification.herald.dto.otp.OtpEmailTarget;
import com.notification.herald.dto.otp.OtpRequestDto;
import com.notification.herald.dto.otp.OtpSmsTarget;
import com.notification.herald.dto.otp.OtpValidateDto;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

  @Mock private EmailNotificationService emailNotificationService;

  @Mock private SMSNotificationService smsNotificationService;

  @Mock private RedisTemplate<String, String> redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  @InjectMocks private OtpService otpService;

  private ResponseDto created(String id) {
    return new ResponseDto(List.of(id), HttpStatus.CREATED.value());
  }

  @Test
  void requestOtp_withEmailOnly_shouldSendEmailNotificationOnly() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(emailNotificationService.sendEmail(any())).thenReturn(created("req-123"));

    OtpRequestDto request =
        new OtpRequestDto(
            new OtpEmailTarget("user@example.com", "Alice"), null, "Your OTP is ${OTP}", 300);

    ResponseDto response = otpService.requestOtp(request);

    verify(emailNotificationService).sendEmail(any());
    verify(smsNotificationService, never()).sendSms(any());
    assertThat(response.status()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.data()).isInstanceOf(String.class);
  }

  @Test
  void requestOtp_withSmsOnly_shouldSendSmsNotificationOnly() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(smsNotificationService.sendSms(any())).thenReturn(created("req-456"));

    OtpRequestDto request =
        new OtpRequestDto(null, new OtpSmsTarget("+1234567890"), "Your OTP is ${OTP}", 300);

    otpService.requestOtp(request);

    verify(smsNotificationService).sendSms(any());
    verify(emailNotificationService, never()).sendEmail(any());
  }

  @Test
  void requestOtp_withBothChannels_shouldSendBothWithSameCode() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(emailNotificationService.sendEmail(any())).thenReturn(created("req-1"));
    when(smsNotificationService.sendSms(any())).thenReturn(created("req-2"));

    OtpRequestDto request =
        new OtpRequestDto(
            new OtpEmailTarget("user@example.com", "Bob"),
            new OtpSmsTarget("+1234567890"),
            "OTP: ${OTP}",
            300);

    otpService.requestOtp(request);

    ArgumentCaptor<EmailNotifRequestDto> emailCaptor =
        ArgumentCaptor.forClass(EmailNotifRequestDto.class);
    ArgumentCaptor<SMSNotifRequestDto> smsCaptor =
        ArgumentCaptor.forClass(SMSNotifRequestDto.class);
    verify(emailNotificationService).sendEmail(emailCaptor.capture());
    verify(smsNotificationService).sendSms(smsCaptor.capture());

    // Same generated code delivered on both channels.
    assertThat(emailCaptor.getValue().getContent()).isEqualTo(smsCaptor.getValue().getContent());
  }

  @Test
  void requestOtp_withNoChannel_shouldThrow400() {
    OtpRequestDto request = new OtpRequestDto(null, null, "OTP: ${OTP}", 300);

    assertThatThrownBy(() -> otpService.requestOtp(request))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex ->
                assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST));

    verify(emailNotificationService, never()).sendEmail(any());
    verify(smsNotificationService, never()).sendSms(any());
  }

  @Test
  void requestOtp_shouldReplaceOtpPlaceholderInContent() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(emailNotificationService.sendEmail(any())).thenReturn(created("req-123"));

    OtpRequestDto request =
        new OtpRequestDto(
            new OtpEmailTarget("user@example.com", "Alice"), null, "Code: ${OTP}", 300);

    otpService.requestOtp(request);

    ArgumentCaptor<EmailNotifRequestDto> captor =
        ArgumentCaptor.forClass(EmailNotifRequestDto.class);
    verify(emailNotificationService).sendEmail(captor.capture());
    String content = captor.getValue().getContent();

    assertThat(content).doesNotContain("${OTP}");
    assertThat(content).startsWith("Code: ");
    assertThat(content.replace("Code: ", "")).matches("\\d{5}");
  }

  @Test
  void requestOtp_shouldStoreHashedOtpInRedisUnderReturnedIdWithTtl() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(emailNotificationService.sendEmail(any())).thenReturn(created("req-abc"));

    OtpRequestDto request =
        new OtpRequestDto(
            new OtpEmailTarget("user@example.com", "Alice"), null, "OTP: ${OTP}", 120);

    ResponseDto response = otpService.requestOtp(request);
    String otpId = (String) response.data();

    verify(valueOperations).set(eq("otp:" + otpId), anyString(), eq(Duration.ofSeconds(120)));
  }

  @Test
  void validateOtp_withValidOtp_shouldReturnAuthorizedAndDeleteFromRedis() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    String requestId = "req-123";
    String otp = "54321";
    String hashedOtp = BCrypt.hashpw(otp, BCrypt.gensalt(5));

    when(valueOperations.get("otp:" + requestId)).thenReturn(hashedOtp);

    OtpValidateDto validateDto = new OtpValidateDto();
    validateDto.setRequestId(requestId);
    validateDto.setOtp(otp);

    ResponseDto response = otpService.validateOtp(validateDto);

    assertThat(response.data()).isEqualTo("Authorized");
    assertThat(response.status()).isEqualTo(HttpStatus.OK.value());
    verify(redisTemplate).delete("otp:" + requestId);
  }

  @Test
  void validateOtp_whenOtpNotFoundInRedis_shouldThrow404() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(anyString())).thenReturn(null);

    OtpValidateDto validateDto = new OtpValidateDto();
    validateDto.setRequestId("req-missing");
    validateDto.setOtp("12345");

    assertThatThrownBy(() -> otpService.validateOtp(validateDto))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex ->
                assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND));
  }

  @Test
  void validateOtp_withWrongOtp_shouldThrow401() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    String requestId = "req-123";
    String hashedOtp = BCrypt.hashpw("99999", BCrypt.gensalt(5));

    when(valueOperations.get("otp:" + requestId)).thenReturn(hashedOtp);

    OtpValidateDto validateDto = new OtpValidateDto();
    validateDto.setRequestId(requestId);
    validateDto.setOtp("11111");

    assertThatThrownBy(() -> otpService.validateOtp(validateDto))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex ->
                assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.UNAUTHORIZED));
  }

  @Test
  void validateOtp_withWrongOtp_shouldNotDeleteFromRedis() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    String requestId = "req-123";
    String hashedOtp = BCrypt.hashpw("99999", BCrypt.gensalt(5));

    when(valueOperations.get("otp:" + requestId)).thenReturn(hashedOtp);

    OtpValidateDto validateDto = new OtpValidateDto();
    validateDto.setRequestId(requestId);
    validateDto.setOtp("11111");

    assertThatThrownBy(() -> otpService.validateOtp(validateDto))
        .isInstanceOf(ResponseStatusException.class);

    verify(redisTemplate, never()).delete(anyString());
  }
}
