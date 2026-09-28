package com.nawaf.meetingpoint.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendMeetingInvitation(String to, String organizerName, String meetingName, String meetingTime, String invitationUrl) {
        String html = loadTemplate("templates/email/meeting-invitation.html");

        html = html.replace("{{organizerName}}", organizerName).replace("{{meetingName}}", meetingName).replace("{{meetingTime}}", meetingTime).replace("{{invitationUrl}}", invitationUrl);

        sendHtmlEmail(to, "Meeting Invitation - " + meetingName, html);
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);

            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }
    }

    public void sendWelcomeEmail(String to, String userName) {
        String html = """
            <html>
                <body>
                    <h2>Welcome to Liqaa, %s!</h2>
                    <p>Your account has been created successfully.</p>
                    <p>You can now create meetings, join invitations, and find the best meeting place with your friends.</p>
                    <p>Thank you for using Liqaa.</p>
                </body>
            </html>
            """.formatted(userName);

        sendHtmlEmail(to, "Welcome to Liqaa", html);
    }

    public void sendMeetingConfirmedEmail(String to, String userName, String meetingName, String placeName, String googleMapsUrl) {
        String html = """
            <html>
                <body>
                    <h2>Meeting Confirmed!</h2>
                    <p>Hello %s,</p>
                    <p>Your meeting <strong>%s</strong> has been confirmed.</p>
                    <p>Selected place: <strong>%s</strong></p>
                    <p><a href="%s">Open Place in Google Maps</a></p>
                    <p>See you there!</p>
                </body>
            </html>
            """.formatted(userName, meetingName, placeName, googleMapsUrl);

        sendHtmlEmail(to, "Meeting Confirmed - " + meetingName, html);
    }
}