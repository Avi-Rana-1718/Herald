package com.notification.herald.providers.mail;

import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.mail.Mailjet.request.MailjetRequestDto;
import com.notification.herald.dto.mail.Mailjet.request.MailjetRequestMessages;
import com.notification.herald.dto.mail.Mailjet.response.MailjetResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MailjetImpl implements MailProvider {

    private final RestClient mailClient;

    MailjetImpl(@Qualifier("mailClient") RestClient mailClient) {
        this.mailClient = mailClient;
    }

    @Override
    public String sendMail(MailRequestDto request) {
        MailjetRequestDto requestDto = this.transform(request);

        MailjetResponseDto response = mailClient.post().uri("send").contentType(MediaType.APPLICATION_JSON).body(requestDto).retrieve().body(MailjetResponseDto.class);
        return response.Messages().getFirst().To().getFirst().MessageID();
    };

    @Override
    public void setStatus(String requestId) {

    }

    private MailjetRequestDto transform(MailRequestDto requestDto) {
        MailjetRequestMessages message = new MailjetRequestMessages(requestDto.from(), requestDto.to(), requestDto.cc(), requestDto.bcc(), requestDto.subject(), requestDto.textPart(), requestDto.HTMLPart());
        List<MailjetRequestMessages> messages = new ArrayList<>();
        messages.add(message);
        return new MailjetRequestDto(messages);
    }
}
