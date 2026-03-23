package com.notification.herald.providers.mail;

import com.notification.herald.configurations.properties.MailjetConfiguration;
import com.notification.herald.dto.mail.MailAddress;
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
import java.util.Optional;

@Service
public class MailjetImpl implements MailProvider {

    private final RestClient mailClient;
    private final MailjetConfiguration mailjetConfiguration;

    public MailjetImpl(@Qualifier("mailjetClient") RestClient mailClient, MailjetConfiguration mailjetConfiguration) {
        this.mailClient = mailClient;
        this.mailjetConfiguration = mailjetConfiguration;
    }

    @Override
    public String sendMail(MailRequestDto request) {
        MailjetRequestDto requestDto = this.transform(request);

        MailjetResponseDto response = mailClient.post().uri("send").contentType(MediaType.APPLICATION_JSON).body(requestDto).retrieve().body(MailjetResponseDto.class);
        return response.Messages().getFirst().To().getFirst().MessageID();
    }

    private MailjetRequestDto transform(MailRequestDto requestDto) {
        List<MailAddress> mailAddresses = new ArrayList<>();
        MailAddress mailAddress = new MailAddress(requestDto.to().email(), requestDto.to().name());
        mailAddresses.add(mailAddress);

        MailjetRequestMessages message = new MailjetRequestMessages(new MailAddress(mailjetConfiguration.email(),
            mailjetConfiguration.name()), mailAddresses, requestDto.subject(), requestDto.content());
        List<MailjetRequestMessages> messages = new ArrayList<>();
        messages.add(message);
        return new MailjetRequestDto(messages);
    }
}
