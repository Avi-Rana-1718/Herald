package com.notification.herald.providers.mail;

import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.mail.Mailjet.MailjetResponseDto;
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

    MailjetImpl(@Qualifier("MailClient") WebClient mailClient) {
        this.mailClient = mailClient;
    }

    @Override
    public Mono<String> sendMail(MailRequestDto request) {
        HashMap<String, Object> mailjetRequest = new HashMap<>();
        List<MailRequestDto> messages = new ArrayList<>();
        messages.add(request);
        mailjetRequest.put("Messages", messages);
        return mailClient.post().uri("send").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mailjetRequest).retrieve().bodyToMono(MailjetResponseDto.class)
                .map(response -> {
                    if(Objects.nonNull(response) && Objects.nonNull(response.Messages()) && !response.Messages().isEmpty()) {
                        return response.Messages().getFirst().To().getFirst().MessageID();
                    }
                    throw new RuntimeException("No Message ID in response");
                });


    };
}
