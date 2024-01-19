package com.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.demo.model.Student;
import com.demo.service.StudentService;

@Controller
public class studentController {

	private final StudentService service;

	public studentController(StudentService service) {
		this.service = service;
	}

	@GetMapping("/students")
	public String studentList(Model model) {
		return findPaginated(1, model);
	}

	@GetMapping("/studentForm")
	public String studentFormForSave(Model model) {
		Student student = new Student();
		model.addAttribute("student", student);
		return "studentForm";
	}

	@PostMapping("/saveStudent")
	public String saveStudent(@ModelAttribute Student student) {
		String msg = service.saveStudent(student);
		if (msg.equals("existStudent")) {
			return "redirect:/studentForm?existStudent";
		}
		if (msg.equals("notValidName")) {
			return "redirect:/studentForm?notValidName";
		}
		if (msg.equals("notValidEmail")) {
			return "redirect:/studentForm?notValidEmail";
		}

		return "redirect:/studentForm?saved";
	}

	@GetMapping("/studentFormForUpdate/{id}")
	public String studentFormForUpdate(@PathVariable Long id, Model model) {
		Student student = service.findStudentById(id);
		model.addAttribute("student", student);
		return "updateForm";
	}

	@PostMapping("/updateStudent")
	public String updateStudent(@ModelAttribute Student student) {
		String msg = service.saveStudent(student);

		if (msg.equals("notValidName")) {
			return "redirect:/students?notValidName";
		}

		if (msg.equals("notValidEmail")) {
			return "redirect:/students?notValidEmail";
		}
		return "redirect:/students?success";
	}

	@GetMapping("/deleteStudent/{id}")
	public String deleteStudent(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<String> roles = new ArrayList<>();
		if (authentication != null && authentication.isAuthenticated()) {
			authentication.getAuthorities().forEach(authority -> roles.add(authority.getAuthority()));
		}
		if (!roles.contains("ROLE_ADMIN")) {
			return "redirect:/students?notAdmin";
		}
		service.deleteStudent(id);
		return "redirect:/students";
	}

	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {

		int pageSize = 5;
		Page<Student> page = service.findPaginated(pageNo, pageSize);
		List<Student> students = page.getContent();

		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("students", students);
		return "studentList";
	}

}
