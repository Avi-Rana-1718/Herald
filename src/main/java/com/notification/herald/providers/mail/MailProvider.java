package main.java.com.notification.herald.providers.mail;

public abstract class MailProvider {
    // return requestId
   public abstract String sendMail(MailRequestDto request);
}
