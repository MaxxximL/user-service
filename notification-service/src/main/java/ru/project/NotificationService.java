package ru.project;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(UserEvent event) {
        String subject;
        String text;

        if ("USER_CREATED".equals(event.getEventType())) {
            subject = "Аккаунт успешно создан";
            text = "Здравствуйте, " + event.getName() + "!\n\nВаш аккаунт на сайте был успешно создан.";
        } else if ("USER_DELETED".equals(event.getEventType())) {
            subject = "Аккаунт был удалён";
            text = "Здравствуйте!\n\nВаш аккаунт был удалён.";
        } else {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getEmail());
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("no-reply@yourdomain.com");

        mailSender.send(message);
    }
}