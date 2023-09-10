package com.example.education_center.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaEmailSender;



    public void sendEmail(String to, String subject, String body){
        MimeMessage message = javaEmailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
        try{
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        javaEmailSender.send(message);

    }

    public void sendBirthdayEmail(String name, String to){
        SpringTemplateEngine springTemplateEngine=new SpringTemplateEngine();
        String subject = "Happy Birthday! "+name;
        Context ctx = new Context();
        ctx.setVariable("name", name);

        String body = springTemplateEngine.process("email/hpbd.html",ctx);
        sendEmail(to, subject, body);
    }

    public void sendNotiEmail( String to, String msg, String course_name){
        SpringTemplateEngine springTemplateEngine=new SpringTemplateEngine();
        String subject = "You have new notification in "+course_name;
        Context ctx = new Context();
        ctx.setVariable("msg", msg);

        String body = springTemplateEngine.process("email/noti.html",ctx);
        sendEmail(to, subject, body);
    }

}
