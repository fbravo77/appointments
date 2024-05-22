package com.benefits.appointments.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  public void sendEmail(String[] to, String subject, String body) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    message.setRecipients(MimeMessage.RecipientType.TO, String.join(",", Arrays.stream(to).filter(Objects::nonNull).toArray(String[]::new)));
    message.setSubject(subject);
    message. setContent(body, "text/html; charset=utf-8");
    mailSender.send(message);
  }
}
