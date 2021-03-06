package com.cgfix.japi.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cgfix.japi.AbstractTest;

public class UserControllerTest extends AbstractTest {
	private static final String BASE_URL = "/api/users";

	@Test
	@WithUserDetails(value = "admin")
	public void getCurrentUser() throws Exception {
		String path = BASE_URL + "/current";
		mvc.perform(MockMvcRequestBuilders.get(path)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.username", is("admin")))
				.andExpect(jsonPath("$.firstName").isString()).andExpect(jsonPath("$.lastName").isString());

	}

}
