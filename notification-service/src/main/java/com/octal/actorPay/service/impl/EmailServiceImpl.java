package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.feign.CmsServiceFeignClient;
import com.octal.actorPay.model.MailProperties;
import com.octal.actorPay.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Objects;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String fromMail;

    @Autowired
    private CmsServiceFeignClient cmsServiceFeignClient;

    @Override
    public void sendEmail(MailProperties mail) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        // helper.addAttachment("template-cover.png", new ClassPathResource("javabydeveloper-email.PNG"));

        Context context = new Context();
        context.setVariables(mail.getProps());
        LinkedHashMap<String, String> data = getContentFromEmailTemplate(mail.getTemplateName());
        if (Objects.nonNull(data) && !data.isEmpty()) {
            String content = data.get("contents");
            String subject = data.get("emailSubject");
            //        String html = templateEngine.process(mail.getTemplateName(), context);
            String html = templateEngine.process(content, context);
            helper.setTo(mail.getMailTo());
            helper.setText(html, true);
            helper.setSubject(subject);
            helper.setFrom(fromMail,"Actor Pay");

            emailSender.send(message);
        }

    }

    private LinkedHashMap<String, String> getContentFromEmailTemplate(String templateName) {

        ApiResponse response = cmsServiceFeignClient.getEmailTemplateBySlug(templateName);
        if (!response.getHttpStatus().is2xxSuccessful()) {
            throw new ObjectNotFoundException("Template not found for the given template name: " + templateName);
        }

        return (LinkedHashMap) response.getData();

    }

}