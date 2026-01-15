import main.java.com.notification.herald.dto.mail.MailRequestFrom;
import main.java.com.notification.herald.dto.mail.MailRequestTo;

public record MailRequestDto(
        String Subject,
        String TextPart,
        String HTMLPart,
        MailRequestTo To,
        MailRequestFrom From) {
}
