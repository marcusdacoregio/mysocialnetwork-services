package com.dooall.mysocialnetwork.profile;

import com.dooall.mysocialnetwork.TestcontainersConfiguration;
import com.dooall.mysocialnetwork.profiles.Profile;
import com.dooall.mysocialnetwork.profiles.ProfileAuthz;
import com.dooall.mysocialnetwork.profiles.ProfileRegistrationRequest;
import com.dooall.mysocialnetwork.profiles.ProfileRegistrationResponse;
import com.dooall.mysocialnetwork.profiles.ProfileRepository;
import com.dooall.mysocialnetwork.users.User;
import com.dooall.mysocialnetwork.users.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rolls back database changes after each test
class ProfileRegistrationControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ProfileAuthz profileAuthz;

	@BeforeEach
	void setUp() {
		// Clean up database before each test if not relying solely on @Transactional,
		// or if @Transactional is on class level and you need specific setup per method.
		// For this setup, @Transactional on the class should suffice.
	}

	@Test
	@DisplayName("Should register new profile successfully when valid data is provided")
	void shouldRegisterNewProfileSuccessfully() throws Exception {
		// Arrange
		String email = "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
		String handle = "testhandle_" + UUID.randomUUID().toString().substring(0, 8);
		ProfileRegistrationRequest request = new ProfileRegistrationRequest(
				email,
				"password123",
				"Test User Display Name",
				handle
		);

		// Act
		MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated()) // Expect HTTP 201 Created
				.andExpect(jsonPath("$.userId").isNotEmpty())
				.andExpect(jsonPath("$.profileId").isNotEmpty())
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.displayName").value("Test User Display Name"))
				.andExpect(jsonPath("$.handle").value(handle))
				.andReturn();

		// Assert - Response DTO
		String responseBody = mvcResult.getResponse().getContentAsString();
		ProfileRegistrationResponse responseDto = this.objectMapper.readValue(responseBody, ProfileRegistrationResponse.class);

		assertThat(responseDto.userId()).isNotNull();
		assertThat(responseDto.profileId()).isNotNull();
		assertThat(responseDto.email()).isEqualTo(email);
		assertThat(responseDto.displayName()).isEqualTo("Test User Display Name");
		assertThat(responseDto.handle()).isEqualTo(handle);

		// Assert - Database state
		Optional<User> createdUserOpt = this.userRepository.findById(responseDto.userId());
		assertThat(createdUserOpt).isPresent();
		User createdUser = createdUserOpt.get();
		assertThat(createdUser.getEmail()).isEqualTo(email);
		assertThat(this.passwordEncoder.matches("password123", createdUser.getPasswordHash())).isTrue();
		assertThat(createdUser.getCreatedAt()).isNotNull();

		Optional<Profile> createdProfileOpt = this.profileRepository.findById(responseDto.profileId());
		assertThat(createdProfileOpt).isPresent();
		Profile createdProfile = createdProfileOpt.get();
		assertThat(createdProfile.getUserId()).isEqualTo(createdUser.getId());
		assertThat(createdProfile.getDisplayName()).isEqualTo("Test User Display Name");
		assertThat(createdProfile.getHandle()).isEqualTo(handle);
		assertThat(createdProfile.getCreatedAt()).isNotNull();

		assertThat(this.profileAuthz.canEdit(createdUser.getId(), createdProfile.getId())).isTrue();

		// Can log in with email and password
		this.mockMvc.perform(formLogin().loginProcessingUrl("/api/v1/login").user(email).password("password123"))
				.andExpectAll(
						status().isOk(),
						header().exists("SESSION"));
	}

}
