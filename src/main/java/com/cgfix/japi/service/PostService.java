package com.cgfix.japi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.cgfix.japi.exception.BadRequestException;
import com.cgfix.japi.exception.ResourceNotFoundException;
import com.cgfix.japi.model.post.Post;
import com.cgfix.japi.model.role.RoleName;
import com.cgfix.japi.model.user.User;
import com.cgfix.japi.payload.ApiResponse;
import com.cgfix.japi.payload.PagedResponse;
import com.cgfix.japi.payload.PostRequest;
import com.cgfix.japi.payload.PostResponse;
import com.cgfix.japi.repository.PostRepository;
import com.cgfix.japi.repository.UserRepository;
import com.cgfix.japi.security.UserPrincipal;
import com.cgfix.japi.util.AppConstants;
import com.cgfix.japi.util.AppUtils;

@Service
public class PostService {
	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	public PagedResponse<Post> getAllPosts(int page, int size) {
		validatePageNumberAndSize(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

		Page<Post> posts = postRepository.findAll(pageable);

		if (posts.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), posts.getNumber(), posts.getSize(),
					posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
		}

		return new PagedResponse<>(posts.getContent(), posts.getNumber(), posts.getSize(), posts.getTotalElements(),
				posts.getTotalPages(), posts.isLast());
	}

	public PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size) {
		validatePageNumberAndSize(page, size);
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		Page<Post> posts = postRepository.findByCreatedBy(user.getId(), pageable);

		if (posts.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), posts.getNumber(), posts.getSize(),
					posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
		}
		return new PagedResponse<>(posts.getContent(), posts.getNumber(), posts.getSize(), posts.getTotalElements(),
				posts.getTotalPages(), posts.isLast());
	}


	public ResponseEntity<?> updatePost(Long id, PostRequest newPostRequest, UserPrincipal currentUser) {
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

		if (post.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			post.setTitle(newPostRequest.getTitle());
			post.setBody(newPostRequest.getBody());
			Post updatedPost = postRepository.save(post);
			return new ResponseEntity<>(updatedPost, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to edit this post"),
				HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> deletePost(Long id, UserPrincipal currentUser) {
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		if (post.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			postRepository.deleteById(id);
			return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted post"), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(true, "You don't have permission to delete this post"),
				HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> addPost(PostRequest postRequest, UserPrincipal currentUser) {
		User user = userRepository.findById(currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", 1L));

		Post post = new Post();
		post.setBody(postRequest.getBody());
		post.setTitle(postRequest.getTitle());
		post.setUser(user);

		Post newPost = postRepository.save(post);

		PostResponse postResponse = new PostResponse();

		postResponse.setTitle(newPost.getTitle());
		postResponse.setBody(newPost.getBody());

		return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
	}

	public ResponseEntity<?> getPost(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		return new ResponseEntity<>(post, HttpStatus.OK);
	}

	private void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BadRequestException("Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new BadRequestException("Size number cannot be less than zero.");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
		}
	}
}
