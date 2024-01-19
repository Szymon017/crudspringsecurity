package com.demo.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.demo.model.Student;
import com.demo.repository.StudentRepository;

@Service
public class StudentServiceImp implements StudentService {

	private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	@Autowired
	private StudentRepository repository;

	@Override
	public List<Student> allStudents() {
		return repository.findAll();
	}

	@Override
	public String saveStudent(Student student) {
		Student existStudent = findByEmail(student.getEmail());

		if (!validateStudent(student)) {
			return "notValidName";
		}

		if (!validateStudentEmail(student)) {
			return "notValidEmail";
		}

		if (existStudent != null) {
			return "existStudent";
		}

		repository.save(student);
		return "";
	}

	@Override
	public Student findStudentById(Long id) {
		Student existStudent = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Student Not Found with Id: " + id));
		return existStudent;
	}

	@Override
	public void deleteStudent(Long id) {
		repository.deleteById(id);

	}

	@Override
	public Page<Student> findPaginated(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.repository.findAll(pageable);
	}

	@Override
	public Student findByEmail(String email) {
		return repository.findByEmail(email);
	}

	private boolean validateStudent(Student student) {
		if (student == null || student.getName().isBlank() || student.getAdress().isBlank()) {
			return false;
		}
		return true;
	}

	public boolean validateStudentEmail(Student student) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(student.getEmail());
		return matcher.matches();
	}

}
