package com.neoflies.mystackoverflowapi.services;

import com.neoflies.mystackoverflowapi.domains.EmailConfirmationCode;
import com.neoflies.mystackoverflowapi.domains.User;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
import com.neoflies.mystackoverflowapi.repositories.EmailConfirmationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Component
public class EmailService {
  @Autowired
  JavaMailSender javaMailSender;

  @Autowired
  EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  @Autowired
  SpringTemplateEngine thymeleafTemplateEngine;

  public void sendSimpleEmail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("tienloinguyen22@gmail.com");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    this.javaMailSender.send(message);
  }

  public void sendHtmlEmail(String to, String subject, String html) throws MessagingException {
    MimeMessage message = this.javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setFrom("tienloinguyen22@gmail.com");
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(html, true);
    this.javaMailSender.send(message);
  }

  public void sendConfirmationEmail(User user) {
    EmailConfirmationCode emailConfirmationCode = new EmailConfirmationCode();
    emailConfirmationCode.setCode(UUID.randomUUID());
    emailConfirmationCode.setExpires(new Date(new Date().getTime() + 3600 * 1000));
    emailConfirmationCode.setUser(user);
    this.emailConfirmationCodeRepository.save(emailConfirmationCode);

    List<String> names = Arrays.asList(user.getFirstName(), user.getLastName());
    String recipientName = String.join(" ", names);
    String confirmUrl = String.format("http://localhost:3003/email-confirm/%s", emailConfirmationCode.getCode());
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("recipientName", recipientName);
    templateModel.put("confirmUrl", confirmUrl);

    Context thymeleafContext = new Context();
    thymeleafContext.setVariables(templateModel);
    String html = thymeleafTemplateEngine.process("email-confirmation.html", thymeleafContext);
    try {
      this.sendHtmlEmail(
        user.getEmail(),
        "Neoflies - Email confirmation",
        html
      );
    } catch (MessagingException e) {
      throw new BadRequestException("email/message-exception", "Message exception");
    }
  }
}
