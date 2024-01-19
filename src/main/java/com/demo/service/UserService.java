package com.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.model.Privilege;
import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.PrivilegeRepository;
import com.demo.repository.RoleRepository;
import com.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PrivilegeRepository privilegeRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(email);
		if (user == null) {
			throw new UsernameNotFoundException("This user is not exist");
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			role.getPrivileges().forEach(priv -> {
				authorities.add(new SimpleGrantedAuthority(priv.getName()));
			});
			authorities.add(new SimpleGrantedAuthority(role.getName()));

		});
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);

	}

	public String addUser(User user) {
		User existUser = findByUserName(user.getUsername());
		if (existUser != null) {
			return "existUser";
		}

		if (!validateUser(user)) {
			return "notValid";
		}

		Role roleUser = roleRepository.findByName("ROLE_USER");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.getRoles().add(roleUser);
		userRepository.save(user);
		return "";
	}

	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
	@Transactional

	public User updateUser(User user) {
		User existUser = userRepository.findById(user.getId())
				.orElseThrow(() -> new UsernameNotFoundException("This User is doesn't Exist!"));
		existUser.setUsername(user.getUsername());
		existUser.setPassword(user.getPassword());
		return userRepository.save(existUser);

	}

	public User findByUserName(String username) {
		return userRepository.findByUsername(username);
	}

	private boolean validateUser(User user) {
		if (user == null || user.getUsername().isBlank() || user.getPassword().isBlank()) {
			return false;
		}

		return true;
	}

	public void createAdmin() {
		User admin = null;
		List<Privilege> AllPrivileges = Arrays.asList(new Privilege("ADD"), new Privilege("UPDATE"),
				new Privilege("DELETE"), new Privilege("VIEW"));
		List<Privilege> adminPrivileges = privilegeRepository.saveAll(AllPrivileges);
		List<Privilege> userPrivileges = privilegeRepository.findByNameIn(Arrays.asList("ADD", "UPDATE", "VIEW"));

		Role adminRoles = new Role("ROLE_ADMIN", adminPrivileges);
		Role userRoles = new Role("ROLE_USER", userPrivileges);
		List<Role> roles = Arrays.asList(adminRoles, userRoles);
		roleRepository.saveAll(roles);

		admin = new User("admin", passwordEncoder.encode("admin"), List.of(adminRoles));
		userRepository.save(admin);
	}

}
