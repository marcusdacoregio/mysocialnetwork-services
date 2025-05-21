package com.dooall.mysocialnetwork.profiles.controller;

import com.dooall.mysocialnetwork.profiles.ProfileRegistrationRequest;
import com.dooall.mysocialnetwork.profiles.ProfileRegistrationResponse;
import com.dooall.mysocialnetwork.profiles.ProfileRegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/register")
class ProfileRegistrationController {

	private static final Logger log = LoggerFactory.getLogger(ProfileRegistrationController.class);

	private final ProfileRegistrationService registrationService;

	public ProfileRegistrationController(ProfileRegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@PostMapping
	public ResponseEntity<?> registerProfile(@Valid @RequestBody ProfileRegistrationRequest request) {
		log.info("Received registration request for handle: {}", request.handle());
		try {
			ProfileRegistrationResponse response = this.registrationService.registerNewProfile(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			log.warn("Registration failed due to bad request: {}", e.getMessage());
			// Consider a structured error response DTO for production
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected error occurred during registration for handle {}: {}", request.handle(), e.getMessage(), e);
			// Consider a structured error response DTO for production
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during registration.");
		}
	}
}
