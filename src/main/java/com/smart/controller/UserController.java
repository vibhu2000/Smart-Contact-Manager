package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println(userName);
		
		//get the user using username(email)
		User user = userRepository.getUserByUserName(userName);
		System.out.println(user);
		model.addAttribute("user", user);
	}
	
	
	//user dashboard
	@RequestMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	//open add form handler
	@GetMapping("add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal, 
			HttpSession session){
		
		try {
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);

//		processing and uploading file....
		if(file.isEmpty()) {
			System.out.println("File is empty...");
			contact.setImage("contact.jpg");
		}
		else {
			contact.setImage(file.getOriginalFilename());
			
			File saveFile = new ClassPathResource("static/images").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.print("Image is uploaded");
		}
		
		contact.setUser(user);
		//		iss user ke pass ek contact list hoye gi ussi contact list me add krenge
		user.getContacts().add(contact);
//		updating user list
		this.userRepository.save(user);
		
		System.out.println(contact);
		System.out.println("Added to database");
		
		//success message
		session.setAttribute("message", new Message("Your contact is added.... Add more...", "success"));
		}
		catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
			
			//error message
			session.setAttribute("message", new Message("Something went wrong...", "danger"));
		}
		
		return "normal/add_contact_form";
	}
	
//	Show contact handler
//	per page = 5
//	currnet page = 0[page]
	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Contact List");
		
		//contact ki list ko bhejna hai
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		//PAgable contain these 2 data
		//current page and contact per page 
		Pageable pageable  = PageRequest.of(page, 8);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		
		
		return "normal/show_contact";
	}
	
	
	//showing specific contact detial
	@GetMapping("/contact/{id}")
	public String showContactDetails(@PathVariable("id") Integer id, Model m, Principal principal) {
		System.out.println(id);
		Optional<Contact> contactOptional = this.contactRepository.findById(id);
		Contact contact = contactOptional.get();
		
		//konsa person login hai
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		
		if(user.getId()==contact.getUser().getId()) {
			m.addAttribute("contact", contact);
			m.addAttribute("title", contact.getName());
		}
		
		
		return "normal/contact_details";
	}
	
	
	//delete contact handler
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") Integer id, Model m, HttpSession session, Principal principal) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(id);
		Contact contact = contactOptional.get();
		
//		//jo contact hai  unhe users se unlink krna pdhe ga
//		contact.setUser(null);
//		
//		//jo user login vahi apne contact ko delete kr sake  
//		this.contactRepository.delete(contact);
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		
		session.setAttribute("message", new Message("COntact deleted successfully....", "success"));
		
		return "redirect:/user/show-contact/0";
	}
	
	
	//open update form handler
	@PostMapping("/update-contact/{id}")
	public String updateForm(@PathVariable("id") Integer id, Model m) {
		
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(id).get();
		m.addAttribute("contact", contact);
		
		
		return "normal/update_form";
	}
	
	//update contact handler
	@PostMapping("/process-update")
	public String updatehandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model m, HttpSession session, Principal principal) {
		try {
			
			//old contact detail
			Contact oldcontactDetails = this.contactRepository.findById(contact.getId()).get();
			
			
			
			//image
			if(!file.isEmpty()) {
				
				//delete old photo
				File deleteFile = new ClassPathResource("static/images").getFile();
				File file1 = new File(deleteFile, oldcontactDetails.getImage());
				file1.delete();
				
				//update new photo
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}
			else {
				contact.setImage(oldcontactDetails.getImage());
			}
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact is updated...", "success"));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/contact/"+contact.getId();
	}
	
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
	
	// open setting handler
	@GetMapping("/setting")
	public String openSetting(Model m) {
		
		m.addAttribute("title", "Settings");
		
		return "normal/setting";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session ) {
		System.out.println(oldPassword+" "+ newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			//change the password...
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password is updated...", "success"));
		}else {
			//error..
			session.setAttribute("message", new Message("Wrong old password", "danger"));
			return "redirect:/user/setting";
		}
		
		return "redirect:/user/dashboard";
	}
	
	
	
}
