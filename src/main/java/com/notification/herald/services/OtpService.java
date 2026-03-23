package com.notification.herald.services;

import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.UserDto;
import com.notification.herald.dto.otp.OtpRequestDto;
import com.notification.herald.dto.otp.OtpValidateDto;
import com.notification.herald.enums.NotifTypeEnum;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int OTP_LENGTH = 5;
    private final RedisTemplate<String, String> redisTemplate;

    public ResponseDto requestOtp(OtpRequestDto requestDto) {
        String otp = this.generateOtp();
        String recipientName = requestDto.recipientName();
        String email = requestDto.toEmail();
        String phoneNumber = requestDto.toMobile();
        String content = requestDto.content().replace("${OTP}", otp);
        String subject = "OTP request";

        List<NotifRequestDto> notifRequest = new ArrayList<>();

        if(Objects.nonNull(requestDto.toEmail())) {
             NotifRequestDto notifRequestDto = new NotifRequestDto(NotifTypeEnum.EMAIL, null, email, recipientName, content, subject);
             notifRequest.add(notifRequestDto);
        }

        if(Objects.nonNull(requestDto.toMobile())) {
           NotifRequestDto notifRequestDto = new NotifRequestDto(NotifTypeEnum.SMS, phoneNumber, null, null, content, null);
           notifRequest.add(notifRequestDto);
        }

        List<String> requestId = (List<String>) notificationService.sendNotification(notifRequest).data();

        String hashedOtp = BCrypt.hashpw(otp, BCrypt.gensalt(5));
        redisTemplate.opsForValue().set("otp:"+requestId, hashedOtp, Duration.ofSeconds(requestDto.expiresIn()));


        return new ResponseDto(requestId, HttpStatus.OK.value());
    }

    public ResponseDto validateOtp(OtpValidateDto requestDto) {
        String hashedOtp = redisTemplate.opsForValue().get("otp:"+requestDto.getRequestId());

        if(Objects.isNull(hashedOtp)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No OTP found for this requestId");
        }

        if(!BCrypt.checkpw(requestDto.getOtp(), hashedOtp)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP provided");
        }

        redisTemplate.delete("otp:"+requestDto.getRequestId());


        return new ResponseDto("Authorized", HttpStatus.OK.value());
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        for(int i=0;i<OTP_LENGTH;i++) {
            otp.append(secureRandom.nextInt(10));
        }

        return otp.toString();
    }
}
