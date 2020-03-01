package com.cgfix.japi.controller;

import com.cgfix.japi.model.post.Post;
import com.cgfix.japi.model.user.User;
import com.cgfix.japi.payload.*;
import com.cgfix.japi.security.CurrentUser;
import com.cgfix.japi.security.UserPrincipal;
import com.cgfix.japi.service.PostService;
import com.cgfix.japi.service.UserService;
import com.cgfix.japi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private PostService postService;

	@GetMapping("/current")
	@PreAuthorize("hasRole('USER')")
	public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
		return userService.getCurrentUser(currentUser);
	}

	@GetMapping("/{username}/profile")
	public UserProfile getUSerProfile(@PathVariable(value = "username") String username) {
		return userService.getUserProfile(username);
	}

	@GetMapping("/{username}/posts")
	public PagedResponse<Post> getPostsCreatedBy(@PathVariable(value = "username") String username,
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		return postService.getPostsByCreatedBy(username, page, size);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> addUser(@Valid @RequestBody User user) {
		return userService.addUser(user);
	}

	@PutMapping("/{username}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> updateUser(@Valid @RequestBody User newUser,
			@PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser) {
		return userService.updateUser(newUser, username, currentUser);
	}

	@DeleteMapping("/{username}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> deleteUser(@PathVariable(value = "username") String username,
			@CurrentUser UserPrincipal currentUser) {
		return userService.deleteUser(username, currentUser);
	}

}
