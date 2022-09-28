/*
 * 
 */
package com.octal.actorPay.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageHelper {

	@Autowired
	private MessageSource messageSource;

	/**
	 * get message from properties file with arguments
	 * 
	 * @param messageKey - message key
	 * @param arguments  - argument that will be mapped in message argument
	 * @return - message
	 */
	public String getMessage(String messageKey, Object[] arguments) {
		return messageSource.getMessage(messageKey, arguments, Locale.getDefault());
	}

	/**
	 * get message from properties file without arguments
	 * 
	 * @param messageKey - message key
	 * @return - message
	 */
	public String getMessage(String messageKey) {
		return messageSource.getMessage(messageKey, null, Locale.getDefault());
	}
}