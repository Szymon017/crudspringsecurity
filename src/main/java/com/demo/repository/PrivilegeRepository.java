package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege,Long>{

	Privilege findByName(String name);
	
	//@Query("SELECT p FROM Privilege p WHERE p.name in(:list)")
	List<Privilege> findByNameIn(List<String> list);
}
