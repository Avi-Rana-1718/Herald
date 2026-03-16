package com.notification.herald.dto.sms.Twilio;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TwilioResponseDto(

        @JsonProperty("account_sid")
        String accountSid,

        @JsonProperty("api_version")
        String apiVersion,

        @JsonProperty("body")
        String body,

        @JsonProperty("date_created")
        String dateCreated,

        @JsonProperty("date_sent")
        String dateSent,

        @JsonProperty("date_updated")
        String dateUpdated,

        @JsonProperty("direction")
        String direction,

        @JsonProperty("error_code")
        String errorCode,

        @JsonProperty("error_message")
        String errorMessage,

        @JsonProperty("from")
        String from,

        @JsonProperty("messaging_service_sid")
        String messagingServiceSid,

        @JsonProperty("num_media")
        String numMedia,

        @JsonProperty("num_segments")
        String numSegments,

        @JsonProperty("price")
        String price,

        @JsonProperty("price_unit")
        String priceUnit,

        @JsonProperty("sid")
        String sid,

        @JsonProperty("status")
        String status,

        @JsonProperty("subresource_uris")
        SubresourceUris subresourceUris,

        @JsonProperty("to")
        String to,

        @JsonProperty("uri")
        String uri
) {
    public record SubresourceUris(
            @JsonProperty("media")
            String media
    ) {}
}