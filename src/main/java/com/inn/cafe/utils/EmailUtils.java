package com.inn.cafe.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailUtils {

	@Autowired
	private JavaMailSender emailSender;

	public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("azeddine.houasli2018@gmail.com");
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		if (list != null && list.size() > 0) {
			message.setCc(getCcArray(list));
		}
		emailSender.send(message);
	}

	private String[] getCcArray(List<String> ccList) {
		String[] cc = new String[ccList.size()];
		for (int i = 0; i < ccList.size(); i++) {
			cc[i] = ccList.get(i);
		}
		return cc;
	}

	public void forgotMail(String to, String subject, String password) throws MessagingException {

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom("azeddine.houasli2018@gmail.com");
		helper.setTo(to);
		helper.setSubject(subject);
		String htmlMsg = "<p><b>Your Login details for Cafe Management System</b></br><b>Email : </b> " + to
				+ "<br><b>Password: </b> " + password
				+ "<br><a href=\"http://localhost:4200/\">Click here to login </a></p>";
		message.setContent(htmlMsg, "text/html");
		emailSender.send(message);
	}

//	send Set Password Reset
//	public void sendSetPasswordReset(String email) throws MessagingException {
//		MimeMessage mimeMessage = emailSender.createMimeMessage();
//		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
//		mimeMessageHelper.setFrom("azeddine.houasli2018@gmail.com");
//		mimeMessageHelper.setTo(email);
//		mimeMessageHelper.setSubject("Reset Password");
//		mimeMessageHelper.setText(
//				"""
//
//								<div>
//								  <a href="http://localhost:4200/resetPassword" target="_blank">click the link to reset your password</a>
//								</div>
//
//						"""
//						.formatted(email),
//				true);
//
//		emailSender.send(mimeMessage);
//	}

//	<div>
//	  <a href="http://localhost:8080/set-password?email=%s" target="_blank">click link to set password</a>
//	</div>
}
