package com.niit.vay.services;

import com.niit.vay.models.NotificationEmail;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
class MailService {

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    MailService(JavaMailSender mailSender, MailContentBuilder mailContentBuilder) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    @Async
    void sendMail(NotificationEmail notificationEmail) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("Vay");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getBody(), true);
        };

        try {
            mailSender.send(messagePreparator);
            System.out.println("Activation Email Sent!");
        } catch (MailException e) {
            System.out.println("Exception occurred when sending mail " + e.getMessage());
//            throw new Exception("Exception occurred when sending mail to " + notificationEmail.getRecipient().toString());
        }
    }
}
