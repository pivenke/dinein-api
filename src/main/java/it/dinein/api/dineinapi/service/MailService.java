package it.dinein.api.dineinapi.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import static it.dinein.api.dineinapi.common.constant.Email.USERNAME;

@Service
public class MailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    public void sendEmailWithTemplate(String recipientEmail, String subject, String templateName, Reservation reservation, Hotelier hotelier) throws MessagingException {
        // Create a Thymeleaf context with the Quotation and its items
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("reservation", reservation);
        thymeleafContext.setVariable("hotel", hotelier);

        // Render the HTML template with Thymeleaf
        String htmlBody = thymeleafTemplateEngine.process(templateName, thymeleafContext);

        // Create a new email message and set the HTML body
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(USERNAME);
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        // Send the email message
        mailSender.send(message);
    }
}
