package com.dooall.mysocialnetwork.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JdbcAggregateTemplate jdbcAggregateTemplate;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JdbcAggregateTemplate jdbcAggregateTemplate) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jdbcAggregateTemplate = jdbcAggregateTemplate;
	}

	public boolean existsByEmail(String email) {
		log.debug("Checking if email exists: {}", email);
		return this.userRepository.existsByEmail(email);
	}

	/**
	 * Creates a new user. This method is designed to be part of a larger transaction
	 * and will participate if one is already active.
	 *
	 * @param email       The email of the user.
	 * @param rawPassword The raw password to be encoded.
	 * @return A DTO representing the created User.
	 * @throws IllegalArgumentException if the email already exists.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public CreateUserResponse createUser(String email, String rawPassword) { // Changed return type
		log.info("Attempting to create new user with email: {}", email);
		// This check is a safeguard if the method is called directly,
		// though ProfileRegistrationService also performs this check.
		if (this.userRepository.existsByEmail(email)) {
			log.warn("User creation failed: Email {} already exists", email);
			throw new IllegalArgumentException("Email already in use.");
		}
		User newUser = new User(
				email,
				this.passwordEncoder.encode(rawPassword)
		);
		User savedUser = this.jdbcAggregateTemplate.insert(newUser);
		log.info("User created successfully with ID: {}", savedUser.getId());

		return new CreateUserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getCreatedAt());
	}
}