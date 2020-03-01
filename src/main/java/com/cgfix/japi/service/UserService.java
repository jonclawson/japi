package com.cgfix.japi.service;

import com.cgfix.japi.exception.AppException;
import com.cgfix.japi.exception.ResourceNotFoundException;
import com.cgfix.japi.model.role.Role;
import com.cgfix.japi.model.role.RoleName;
import com.cgfix.japi.model.user.User;
import com.cgfix.japi.payload.*;
import com.cgfix.japi.repository.PostRepository;
import com.cgfix.japi.repository.RoleRepository;
import com.cgfix.japi.repository.UserRepository;
import com.cgfix.japi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserSummary getCurrentUser(UserPrincipal currentUser) {
		return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName(),
				currentUser.getLastName());
	}

	public UserProfile getUserProfile(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

		Long postCount = postRepository.countByCreatedBy(user.getId());

		return new UserProfile(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),
				user.getCreatedAt(), user.getEmail(), user.getPhone(), user.getWebsite(),
				postCount);
	}

	public ResponseEntity<?> addUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			return new ResponseEntity<>(new ApiResponse(false, "Username is already taken"), HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>(new ApiResponse(false, "Email is already taken"), HttpStatus.BAD_REQUEST);
		}

		List<Role> roles = new ArrayList<>();
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
		user.setRoles(roles);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User result = userRepository.save(user);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	public ResponseEntity<?> updateUser(User newUser, String username, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setFirstName(newUser.getFirstName());
			user.setLastName(newUser.getLastName());
			user.setPassword(passwordEncoder.encode(newUser.getPassword()));
			user.setPhone(newUser.getPhone());
			user.setWebsite(newUser.getWebsite());

			User updatedUser = userRepository.save(user);
			return new ResponseEntity<>(updatedUser, HttpStatus.OK);

		}

		return new ResponseEntity<>(
				new ApiResponse(false, "You don't have permission to update profile of: " + username),
				HttpStatus.UNAUTHORIZED);

	}

	public ResponseEntity<?> deleteUser(String username, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
		if (!user.getId().equals(currentUser.getId())) {
			return new ResponseEntity<>(
					new ApiResponse(false, "You don't have permission to delete profile of: " + username),
					HttpStatus.UNAUTHORIZED);
		}
		userRepository.deleteById(user.getId());

		return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted profile of: " + username),
				HttpStatus.OK);
	}

}
