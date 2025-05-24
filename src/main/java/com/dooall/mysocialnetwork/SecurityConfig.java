package com.dooall.mysocialnetwork;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers("/api/v1/register").permitAll()
						.anyRequest().authenticated())
				.csrf(CsrfConfigurer::disable)
				.formLogin((login) -> login
						.loginProcessingUrl("/api/v1/login")
						.successHandler((req, resp, auth) -> {
							resp.setStatus(200);
						})
						.failureHandler((req, resp, e) -> resp.setStatus(401))
				)
				.logout((logout) -> logout
						.logoutUrl("/api/v1/logout")
						.logoutSuccessHandler((req, res, authentication) -> res.setStatus(200)))
				.exceptionHandling((exception) -> exception
						.authenticationEntryPoint((req, resp, auth) -> resp.setStatus(401))
						.accessDeniedHandler((req, resp, auth) -> resp.setStatus(403))
				);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		String encodingId = "bcrypt";
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put(encodingId, new BCryptPasswordEncoder(14));
		return new DelegatingPasswordEncoder(encodingId, encoders);
	}

}