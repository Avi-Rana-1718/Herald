package com.notification.herald.services;

import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.otp.OtpRequestDto;
import com.notification.herald.dto.otp.OtpValidateDto;
import com.notification.herald.enums.NotifTypeEnum;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void requestOtp_withEmailOnly_shouldSendEmailNotification() {
        OtpRequestDto request = new OtpRequestDto(null, "user@example.com", "Your OTP is ${OTP}", "Alice", 300);
        List<String> requestIds = List.of("req-123");
        when(notificationService.sendNotification(any())).thenReturn(new ResponseDto(requestIds, HttpStatus.CREATED.value()));

        ResponseDto response = otpService.requestOtp(request);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationService).sendNotification(captor.capture());
        List<NotifRequestDto> notifications = captor.getValue();

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getType()).isEqualTo(NotifTypeEnum.EMAIL);
        assertThat(response).isNotNull();
    }

    @Test
    void requestOtp_withMobileOnly_shouldSendSmsNotification() {
        OtpRequestDto request = new OtpRequestDto("+1234567890", null, "Your OTP is ${OTP}", null, 300);
        List<String> requestIds = List.of("req-456");
        when(notificationService.sendNotification(any())).thenReturn(new ResponseDto(requestIds, HttpStatus.CREATED.value()));

        otpService.requestOtp(request);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationService).sendNotification(captor.capture());
        List<NotifRequestDto> notifications = captor.getValue();

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getType()).isEqualTo(NotifTypeEnum.SMS);
    }

    @Test
    void requestOtp_withBothEmailAndMobile_shouldSendTwoNotifications() {
        OtpRequestDto request = new OtpRequestDto("+1234567890", "user@example.com", "OTP: ${OTP}", "Bob", 300);
        List<String> requestIds = List.of("req-1", "req-2");
        when(notificationService.sendNotification(any())).thenReturn(new ResponseDto(requestIds, HttpStatus.CREATED.value()));

        otpService.requestOtp(request);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationService).sendNotification(captor.capture());

        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void requestOtp_shouldReplaceOtpPlaceholderInContent() {
        OtpRequestDto request = new OtpRequestDto(null, "user@example.com", "Code: ${OTP}", "Alice", 300);
        List<String> requestIds = List.of("req-123");
        when(notificationService.sendNotification(any())).thenReturn(new ResponseDto(requestIds, HttpStatus.CREATED.value()));

        otpService.requestOtp(request);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationService).sendNotification(captor.capture());
        String content = ((NotifRequestDto) captor.getValue().get(0)).getContent();

        assertThat(content).doesNotContain("${OTP}");
        assertThat(content).startsWith("Code: ");
        // The replaced OTP should be 5 digits
        String otp = content.replace("Code: ", "");
        assertThat(otp).matches("\\d{5}");
    }

    @Test
    void requestOtp_shouldStoreHashedOtpInRedisWithTtl() {
        OtpRequestDto request = new OtpRequestDto(null, "user@example.com", "OTP: ${OTP}", "Alice", 120);
        List<String> requestIds = List.of("req-abc");
        when(notificationService.sendNotification(any())).thenReturn(new ResponseDto(requestIds, HttpStatus.CREATED.value()));

        otpService.requestOtp(request);

        verify(valueOperations).set(eq("otp:" + requestIds), anyString(), eq(Duration.ofSeconds(120)));
    }

    @Test
    void validateOtp_withValidOtp_shouldReturnAuthorizedAndDeleteFromRedis() {
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
        when(valueOperations.get(anyString())).thenReturn(null);

        OtpValidateDto validateDto = new OtpValidateDto();
        validateDto.setRequestId("req-missing");
        validateDto.setOtp("12345");

        assertThatThrownBy(() -> otpService.validateOtp(validateDto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void validateOtp_withWrongOtp_shouldThrow401() {
        String requestId = "req-123";
        String hashedOtp = BCrypt.hashpw("99999", BCrypt.gensalt(5));

        when(valueOperations.get("otp:" + requestId)).thenReturn(hashedOtp);

        OtpValidateDto validateDto = new OtpValidateDto();
        validateDto.setRequestId(requestId);
        validateDto.setOtp("11111");

        assertThatThrownBy(() -> otpService.validateOtp(validateDto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void validateOtp_withWrongOtp_shouldNotDeleteFromRedis() {
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
