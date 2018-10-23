package com.boostani.backend.api.service.email;

import org.springframework.mail.SimpleMailMessage;

/**
 * 
 * @author Loay
 *
 */
public interface EmailService {
	void sendSimpleMessage(String to, String subject, String text) throws Exception;

	void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String... templateArgs) throws Exception;

	void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws Exception;
}
