package com.api.user.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailUtil {
	

	public static void mailSend(String toEmail,String subject,String body) {
		

		final String username = "madhuri.gaikwad198@gmail.com";
		final String password = "madhus198";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			//static sendMail( Session session,String toEmail,String subject,String body) {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("from-email@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(toEmail));
			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);
			
			
			System.out.println("Sent Email Successfully");
			


		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	}
