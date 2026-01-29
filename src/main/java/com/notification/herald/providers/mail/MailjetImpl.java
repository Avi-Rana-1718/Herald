package com.notification.herald.providers.mail;

import com.notification.herald.configurations.MailjetConfiguration;
import com.notification.herald.dto.mail.MailAddress;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.mail.Mailjet.request.MailjetRequestDto;
import com.notification.herald.dto.mail.Mailjet.request.MailjetRequestMessages;
import com.notification.herald.dto.mail.Mailjet.response.MailjetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailjetImpl implements MailProvider {

    private final RestClient mailClient;
    private final MailjetConfiguration mailjetConfiguration;


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
        List<MailAddress> mailAddresses =
            Optional.ofNullable(requestDto.to())
                .orElse(List.of())
                .stream()
                .filter(u -> u.email() != null)
                .map(u -> new MailAddress(u.email(), u.name()))
                .toList();

        MailjetRequestMessages message = new MailjetRequestMessages(new MailAddress(mailjetConfiguration.email(),
            mailjetConfiguration.name()), mailAddresses, requestDto.subject(), requestDto.content());
        List<MailjetRequestMessages> messages = new ArrayList<>();
        messages.add(message);
        return new MailjetRequestDto(messages);
    }
}
