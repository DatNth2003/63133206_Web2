package com.ntd63133206.bookbuddy.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    public static void sendResetPasswordEmail(String recipientEmail, String resetLink) throws MessagingException {
        String host = "smtp.example.com";
        String port = "587";
        String username = "bookbuddy@gmail.com";
        String password = "ntd63133206";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Đặt lại mật khẩu");

            String emailBody = "Để đặt lại mật khẩu của bạn, hãy nhấp vào liên kết sau:\n\n" + resetLink;

            message.setText(emailBody);

            Transport.send(message);

            System.out.println("Email đặt lại mật khẩu đã được gửi thành công!");
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email đặt lại mật khẩu thất bại", e);
        }
    }
}
