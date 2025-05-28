package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.models.LocalUser;
import java.util.Map;

public interface EmailService {
  void sendEmail(
      String to, String subject, String templateName, Map<String, Object> contextVariables);

  void sendVerificationMail(LocalUser user, String token);

  void sendPasswordResetMail(String to, LocalUser user);
}
