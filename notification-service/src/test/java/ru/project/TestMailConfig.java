package ru.project;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
public class TestMailConfig {

    static GreenMail greenMailInstance;

    @Bean
    public GreenMail greenMail() {
        if (greenMailInstance == null) {
            greenMailInstance = new GreenMail(ServerSetup.SMTP.dynamicPort());
            greenMailInstance.start();
        }
        return greenMailInstance;
    }

    @Bean
    public JavaMailSender mailSender(GreenMail greenMail) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        sender.setPort(greenMail.getSmtp().getPort());
        sender.setUsername(null);
        sender.setPassword(null);

        sender.getJavaMailProperties().put("mail.smtp.auth", "false");
        sender.getJavaMailProperties().put("mail.smtp.starttls.enable", "false");
        sender.getJavaMailProperties().put("mail.debug", "true");

        return sender;
    }
}