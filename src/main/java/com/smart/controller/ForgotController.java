package com.smart.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {
	Random random = new Random(1000);
	
	//email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}
	
	//send otp handler
	@PostMapping("/send-otp")
	public String SendOTP(@RequestParam("email") String email, Model m) {
		m.addAttribute("title", "email verifivation");
		System.out.println(email);
		
		//generate otp for 4 digit
		
		int otp = random.nextInt(9999);
		
		System.out.println(otp);
		
		return "varify_otp";
	}
}
