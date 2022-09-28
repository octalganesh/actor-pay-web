package com.octal.actorPay;

import com.octal.actorPay.entities.Cms;
import com.octal.actorPay.entities.EmailTemplate;
import com.octal.actorPay.repositories.CmsRepository;
import com.octal.actorPay.repositories.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
@EnableJpaRepositories(basePackages = {"com.octal.actorPay.repositories"})
@ComponentScan("com.octal.actorPay")
public class CmsApplication {
	public static void main(String[] args) {
		SpringApplication.run(CmsApplication.class, args);
	}
}

/**
 * This insert the dummy data into CMS table in DB before starting the service.
 */
@Component
class CMSCommandLineRunner implements CommandLineRunner {

	@Autowired
	private CmsRepository cmsRepository;
	@Autowired
	private EmailTemplateRepository emailRepository;


	@Override
	public void run(String... args) throws Exception {

		if (cmsRepository.findAll().isEmpty()) {
			Cms cmsObj = new Cms();

		cmsObj.setCmsType(1);
		cmsObj.setTitle("About_Us");
		cmsObj.setContents("This is about us CMS data.");
		cmsObj.setMetaTitle("This is meta title data");
		cmsObj.setMetaKeyword("This is meta keyword data term and condition");
		cmsObj.setMetaData("This is meta data ");
		cmsObj.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

		cmsRepository.save(cmsObj);

		Cms cmsObj1 = new Cms();
		cmsObj1.setCmsType(2);
		cmsObj1.setTitle("POLICY");
		cmsObj1.setContents("This is policy related CMS data.");
		cmsObj1.setMetaTitle("This is meta title data");
		cmsObj1.setMetaKeyword("This is meta keyword data policy");
		cmsObj1.setMetaData("This is meta data ");
		cmsObj1.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

		cmsRepository.save(cmsObj1);

		Cms cmsObj2 = new Cms();
		cmsObj2.setCmsType(3);
		cmsObj2.setTitle("TERMS and CONDITIONS");
		cmsObj2.setContents("This is terms related CMS data.");
		cmsObj2.setMetaTitle("This is meta title data");
		cmsObj2.setMetaKeyword("This is meta keyword data t&c");
		cmsObj2.setMetaData("This is meta data ");
		cmsObj2.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

		cmsRepository.save(cmsObj2);
	}
			// e-mail template  dummy data
		if (emailRepository.findAll().isEmpty()) {

			EmailTemplate emailObj = new EmailTemplate();

			emailObj.setTitle("1 - Email Template");
			emailObj.setContents("This is sample data.");
			emailObj.setEmailSubject("<p>This is email template 1.</p><p>This is email template 1.This is email template 1.This is email template 1.This is email template 1.This is email template 1.This is email template 1.</p>");
			emailObj.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

			emailRepository.save(emailObj);

			EmailTemplate emailObj1 = new EmailTemplate();
			emailObj1.setTitle("2 - Email Template");
			emailObj1.setContents("This is sample data.");
			emailObj1.setEmailSubject("<p>This is email template 1.</p><p>This is email template 1.This is email template 1.This is email template 1.This is email template 1.This is email template 1.This is email template 1.</p>");
			emailObj1.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

			emailRepository.save(emailObj1);

			EmailTemplate emailObj2 = new EmailTemplate();
			emailObj2.setTitle("3 - Email Template");
			emailObj2.setContents("This is sample data.");
			emailObj2.setEmailSubject("<p>This is email template 1.</p><p>This is email template 1.This is email template 1.This is email template 1.This is email template 1.This is email template 1.This is email template 1.</p>");
			emailObj2.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

			emailRepository.save(emailObj2);
		}

	}
}