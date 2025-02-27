package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.Exception.MailNotSentException;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired private JavaMailSender javaMailSender;

  @Value("${email.from}")
  private String fromMailAddress;

  @Value("${email.to}")
  private String toMailAddress;

  @Value("${app.frontend.url}")
  private String url;

  private SimpleMailMessage makeMailMessage() {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(fromMailAddress);
    return simpleMailMessage;
  }

  public void sendVerificationMail(VerificationToken verificationToken)
      throws MailNotSentException {
    SimpleMailMessage simpleMailMessage = makeMailMessage();
    simpleMailMessage.setTo(verificationToken.getLocalUser().getEmail());
    simpleMailMessage.setSubject("Verify your Email Address to activate your account");
    simpleMailMessage.setText(
        "please follow this link to verify your email to activate your account"
            + url
            + "/auth/verify?token="
            + verificationToken.getToken());
    try {
      javaMailSender.send(simpleMailMessage);
    } catch (MailException e) {
      throw new MailNotSentException();
    }
  }

  public void sendPasswordRestVerificationMail(String token, LocalUser localUser)
      throws MailNotSentException {
    SimpleMailMessage simpleMailMessage = makeMailMessage();
    simpleMailMessage.setTo(localUser.getEmail());
    simpleMailMessage.setSubject("Password reset Request Link");
    simpleMailMessage.setText(
        "You requested a password reset on our website. Please find the below link to be able to reset your password"
            + url
            + "/auth/reset?token="
            + token);
    try {
      javaMailSender.send(simpleMailMessage);
    } catch (MailException e) {
      throw new MailNotSentException();
    }
  }
}
