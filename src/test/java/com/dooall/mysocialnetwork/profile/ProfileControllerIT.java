package com.dooall.mysocialnetwork.profile;

import com.dooall.mysocialnetwork.CleanupDatabase;
import com.dooall.mysocialnetwork.TestSecurityContexts;
import com.dooall.mysocialnetwork.TestcontainersConfiguration;
import com.dooall.mysocialnetwork.profiles.Profile;
import com.dooall.mysocialnetwork.profiles.ProfileRegistrationRequest;
import com.dooall.mysocialnetwork.profiles.ProfileRegistrationResponse;
import com.dooall.mysocialnetwork.profiles.ProfileRegistrationService;
import com.dooall.mysocialnetwork.profiles.ProfileRepository;
import com.dooall.mysocialnetwork.profiles.ProfileResponse;
import com.dooall.mysocialnetwork.profiles.UpdateHandleRequest;
import com.dooall.mysocialnetwork.profiles.UpdateProfileRequest;
import com.dooall.mysocialnetwork.users.CreateUserResponse;
import com.dooall.mysocialnetwork.users.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@CleanupDatabase
class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRegistrationService profileRegistrationService;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    private UUID testUserId;
    private UUID testProfileId;

    @BeforeEach
    void setUp() {
        String email = "testuser_" + UUID.randomUUID() + "@example.com";
        String password = "password123";

        ProfileRegistrationRequest request = new ProfileRegistrationRequest(email, password,
                "Test User", "testhandle_" + UUID.randomUUID().toString().substring(0, 8));
        ProfileRegistrationResponse profileRegistrationResponse = this.profileRegistrationService.registerNewProfile(request);
        this.testProfileId = profileRegistrationResponse.profileId();
        this.testUserId = profileRegistrationResponse.userId();
    }

    @Test
    @DisplayName("Should update profile display name and bio successfully")
    void shouldUpdateProfileSuccessfully() throws Exception {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Updated Display Name",
                "This is my updated bio"
        );

        // Act & Assert
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/profiles/" + this.testProfileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request))
                        .with(securityContext(TestSecurityContexts.create(this.testUserId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(this.testProfileId.toString()))
                .andExpect(jsonPath("$.userId").value(this.testUserId.toString()))
                .andExpect(jsonPath("$.displayName").value("Updated Display Name"))
                .andExpect(jsonPath("$.bio").value("This is my updated bio"))
                .andReturn();

        // Assert - Response DTO
        String responseBody = mvcResult.getResponse().getContentAsString();
        ProfileResponse responseDto = this.objectMapper.readValue(responseBody, ProfileResponse.class);

        assertThat(responseDto.id()).isEqualTo(this.testProfileId);
        assertThat(responseDto.userId()).isEqualTo(this.testUserId);
        assertThat(responseDto.displayName()).isEqualTo("Updated Display Name");
        assertThat(responseDto.bio()).isEqualTo("This is my updated bio");

        // Assert - Database state
        Optional<Profile> updatedProfileOpt = this.profileRepository.findById(this.testProfileId);
        assertThat(updatedProfileOpt).isPresent();
        Profile updatedProfile = updatedProfileOpt.get();
        assertThat(updatedProfile.getDisplayName()).isEqualTo("Updated Display Name");
        assertThat(updatedProfile.getBio()).isEqualTo("This is my updated bio");
    }

    @Test
    @DisplayName("Should update profile handle successfully")
    void shouldUpdateHandleSuccessfully() throws Exception {
        // Arrange
        String newHandle = "newhandle_" + UUID.randomUUID().toString().substring(0, 8);
        UpdateHandleRequest request = new UpdateHandleRequest(
                newHandle
        );

        // Act & Assert
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/profiles/" + this.testProfileId + "/handle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request))
                        .with(securityContext(TestSecurityContexts.create(this.testUserId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(this.testProfileId.toString()))
                .andExpect(jsonPath("$.userId").value(this.testUserId.toString()))
                .andExpect(jsonPath("$.handle").value(newHandle))
                .andReturn();

        // Assert - Response DTO
        String responseBody = mvcResult.getResponse().getContentAsString();
        ProfileResponse responseDto = this.objectMapper.readValue(responseBody, ProfileResponse.class);

        assertThat(responseDto.id()).isEqualTo(this.testProfileId);
        assertThat(responseDto.userId()).isEqualTo(this.testUserId);
        assertThat(responseDto.handle()).isEqualTo(newHandle);

        // Assert - Database state
        Optional<Profile> updatedProfileOpt = this.profileRepository.findById(this.testProfileId);
        assertThat(updatedProfileOpt).isPresent();
        Profile updatedProfile = updatedProfileOpt.get();
        assertThat(updatedProfile.getHandle()).isEqualTo(newHandle);
    }

    @Test
    @DisplayName("Should return 400 when handle is already taken")
    void shouldReturnBadRequestWhenHandleIsTaken() throws Exception {
        // Arrange - Create another user and profile with a specific handle
        String anotherEmail = "anotheruser_" + UUID.randomUUID() + "@example.com";
        String anotherPassword = "password123";
        CreateUserResponse anotherUserResponse = this.userService.createUser(anotherEmail, anotherPassword);
        UUID anotherUserId = anotherUserResponse.id();

        String existingHandle = "existinghandle_" + UUID.randomUUID().toString().substring(0, 8);
        Profile anotherProfile = new Profile(
                anotherUserId,
                "Another User",
                existingHandle
        );
        anotherProfile = this.jdbcAggregateTemplate.insert(anotherProfile);

        // Create request to update to the existing handle
        UpdateHandleRequest request = new UpdateHandleRequest(
                existingHandle
        );

        // Act & Assert
        this.mockMvc.perform(put("/api/v1/profiles/" + this.testProfileId + "/handle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request))
                        .with(securityContext(TestSecurityContexts.create(this.testUserId))))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert - Database state (handle should not be changed)
        Optional<Profile> updatedProfileOpt = this.profileRepository.findById(this.testProfileId);
        assertThat(updatedProfileOpt).isPresent();
        Profile updatedProfile = updatedProfileOpt.get();
        assertThat(updatedProfile.getHandle()).isNotEqualTo(existingHandle);
    }

    @Test
    @DisplayName("Should return 403 when user is not the profile owner")
    void shouldReturnForbiddenWhenUserIsNotOwner() throws Exception {
        // Arrange - Create another user
        String anotherEmail = "anotheruser_" + UUID.randomUUID() + "@example.com";
        String anotherPassword = "password123";
        CreateUserResponse anotherUserResponse = this.userService.createUser(anotherEmail, anotherPassword);
        UUID anotherUserId = anotherUserResponse.id();

        // Create request with the other user's ID
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Updated Display Name",
                "This is my updated bio"
        );

        // Act & Assert
        this.mockMvc.perform(put("/api/v1/profiles/" + this.testProfileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request))
                        .with(securityContext(TestSecurityContexts.create(anotherUserId))))
                .andExpect(status().isForbidden())
                .andReturn();

        // Assert - Database state (profile should not be changed)
        Optional<Profile> updatedProfileOpt = this.profileRepository.findById(this.testProfileId);
        assertThat(updatedProfileOpt).isPresent();
        Profile updatedProfile = updatedProfileOpt.get();
        assertThat(updatedProfile.getDisplayName()).isEqualTo("Test User");
        assertThat(updatedProfile.getBio()).isNull();
    }
}
