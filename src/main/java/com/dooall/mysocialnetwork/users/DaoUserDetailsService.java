package com.dooall.mysocialnetwork.users;

import com.dooall.mysocialnetwork.core.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class DaoUserDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(DaoUserDetailsService.class);

	private final UserRepository userRepository;

	DaoUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> maybeUser = this.userRepository.findByEmail(email);
		if (maybeUser.isEmpty()) {
			log.warn("User not found with email: {}", email);
			throw new UsernameNotFoundException("Invalid username or password");
		}
		User user = maybeUser.get();
		AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getEmail(), user.getPasswordHash());
		authenticatedUser.setUserId(user.getId());
		return authenticatedUser;
	}

}
