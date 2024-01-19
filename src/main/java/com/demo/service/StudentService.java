package com.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.demo.model.Student;

public interface StudentService {

	List<Student> allStudents();

	String saveStudent(Student student);
	Student findStudentById (Long id);
	void deleteStudent(Long id);
	Student findByEmail(String email);
	Page<Student> findPaginated(int pageNo, int pageSize);

}
