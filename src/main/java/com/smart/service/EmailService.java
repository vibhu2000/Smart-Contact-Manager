//package com.smart.service;
//
//import java.net.Authenticator;
//import java.net.PasswordAuthentication;
//import java.util.Properties;
//
//import org.springframework.stereotype.Service;
//
//import jakarta.websocket.Session;
//
//@Service
//public class EmailService {
//	public boolean sendEmail(String subject, String message, String to) {
//		boolean f = false;
//		String from = "admin@gmail.com";
//		
//		//variable for gmail
//		String host = "smtp.gmail.com";
//		
//		//get the system properties
//		Properties properties = System.getProperties();
//		System.out.println("PROPERTIES "+properties);
//		
//		//setting important information to properties object
//		
//		//host set
//		properties.put("mail.smtp.host", host);
//		properties.put("mail.smtp.port", "465");
//		properties.put("mail.smtp.ssl.enable", "true");
//		properties.put("mail.smtp.auth", "true");
//		
//		//STep-1 to get the session object..
//		Session session = Session.getInstance(properties, new Authenticator() {
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("admin@gmail.com", "");
//			}
//		});
//		
//		session.setDebug(true);
//		
//		//Step-2 
//		
//		
//		
//		return true;
//	}
//}
