package com.dooall.mysocialnetwork;

import com.dooall.mysocialnetwork.core.AuthenticatedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.UUID;

public class TestSecurityContexts {

	public static SecurityContext create(UUID userId) {
		SecurityContextImpl securityContext = new SecurityContextImpl();
		AuthenticatedUser authenticatedUser = new AuthenticatedUser("user", "password");
		authenticatedUser.setUserId(userId);
		var token = UsernamePasswordAuthenticationToken.authenticated(authenticatedUser, authenticatedUser.getPassword(), authenticatedUser.getAuthorities());
		securityContext.setAuthentication(token);
		return securityContext;
	}

}
