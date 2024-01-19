package com.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.model.User;
import com.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserRegistration {

	private final UserService userService;

	@GetMapping("/")
	public String goToRegisterPage(Model model) {
		model.addAttribute("user", new User());
		return "createUser";
	}

	@PostMapping("/register")
	public String Register(@ModelAttribute("user") User user) {
		String msg = userService.addUser(user);

		if (msg.equals("existUser")) {
			return "redirect:/user/?existUser";
		}

		if (msg.equals("notValid")) {
			return "redirect:/user/?notValid";
		}

		return "redirect:/user/?success";
	}

}
