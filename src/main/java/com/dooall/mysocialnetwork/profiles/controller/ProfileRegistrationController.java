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
	public ResponseEntity<ProfileRegistrationResponse> registerProfile(@Valid @RequestBody ProfileRegistrationRequest request) {
		log.info("Received registration request for handle: {}", request.handle());
		ProfileRegistrationResponse response = this.registrationService.registerNewProfile(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
