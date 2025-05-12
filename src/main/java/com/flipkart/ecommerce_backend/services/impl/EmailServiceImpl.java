package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.exception.email.EmailSendException;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  @Value("${email.from}")
  private String fromMailAddress;

  @Value("${app.frontend.url}")
  private String url;

  public void sendEmail(
      String to, String subject, String templateName, Map<String, Object> contextVariables) {
    log.debug("Preparing HTML email. To: {}, Subject: {}, Template: {}", to, subject, templateName);
    Context thymeleafContext = new Context();
    thymeleafContext.setVariables(contextVariables);

    String htmlBody;
    try {
      htmlBody = templateEngine.process(templateName, thymeleafContext);
    } catch (Exception e) {
      log.error("Error processing Thymeleaf template '{}': {}", templateName, e.getMessage(), e);
      throw new EmailSendException(to, "Failed to process email template: " + templateName);
    }

    MimeMessage message = javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(fromMailAddress);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true);

      javaMailSender.send(message);
      log.debug("Email sent successfully to: {}", to);
    } catch (MessagingException | MailException e) {
      log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
      throw new EmailSendException(to, "Failed to send email");
    }
  }

  @Async
  public void sendVerificationMail(LocalUser user, String verificationToken) {
    Map<String, Object> variables =
        Map.of(
            "emailType",
            "Verification",
            "name",
            user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
            "verificationLink",
            url + "/api/verify/email?token=" + verificationToken);
    sendEmail(user.getEmail(), "Verify your Email Address", "welcome.html", variables);
  }

  @Async
  public void sendPasswordResetMail(String token, LocalUser localUser) {
    log.info("Attempting to send password reset email to: {}", localUser.getEmail());
    Map<String, Object> variables =
        Map.of(
            "emailType",
            "PasswordReset",
            "name",
            localUser.getFirstName(),
            "resetLink",
            url + "/api/auth/reset?token=" + token);
    sendEmail(localUser.getEmail(), "Password Reset Request", "password-reset.html", variables);
    log.info("Password reset email sent to: {}", localUser.getEmail());
  }
}
