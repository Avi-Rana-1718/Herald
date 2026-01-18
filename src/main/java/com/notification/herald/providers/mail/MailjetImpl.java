package com.notification.herald.providers.mail;

import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.mail.Mailjet.request.MailjetRequestDto;
import com.notification.herald.dto.mail.Mailjet.request.MailjetRequestMessages;
import com.notification.herald.dto.mail.Mailjet.response.MailjetResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class MailjetImpl implements MailProvider {

    private final WebClient mailClient;

    MailjetImpl(@Qualifier("mailClient") WebClient mailClient) {
        this.mailClient = mailClient;
    }

    @Override
    public Mono<String> sendMail(MailRequestDto request) {
        MailjetRequestDto requestDto = this.transform(request);
        System.out.println("Request payload");
        System.out.println(requestDto.toString());
        return mailClient.post().uri("send").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto).retrieve().bodyToMono(MailjetResponseDto.class)
                .map(response -> {
                    if(Objects.nonNull(response) && Objects.nonNull(response.Messages()) && !response.Messages().isEmpty()) {
                        return response.Messages().getFirst().To().getFirst().MessageID();
                    }
                    throw new RuntimeException("No Message ID in response");
                });
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
