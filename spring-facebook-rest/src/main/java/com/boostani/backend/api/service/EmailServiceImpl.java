package com.boostani.backend.api.service;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Loay
 *
 */
@Component
public class EmailServiceImpl implements EmailService {

	@Autowired
	public JavaMailSender emailSender;

	public void sendSimpleMessage(String to, String subject, String text) throws Exception {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		emailSender.send(message);
	}

	@Override
	public void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template,
			String... templateArgs) throws Exception {
		String text = String.format(template.getText(), templateArgs);
		sendSimpleMessage(to, subject, text);
	}

	@Override
	public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment)
			throws Exception {
		MimeMessage message = emailSender.createMimeMessage();
		// pass 'true' to the constructor to create a multipart message
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(text);

		FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
		helper.addAttachment("Invoice", file);

		emailSender.send(message);
	}
}
