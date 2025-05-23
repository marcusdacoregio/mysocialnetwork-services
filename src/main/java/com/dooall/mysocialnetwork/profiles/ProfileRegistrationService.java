package com.dooall.mysocialnetwork.profiles;

import com.dooall.mysocialnetwork.core.ObjectTypes;
import com.dooall.mysocialnetwork.core.RelationshipStorage;
import com.dooall.mysocialnetwork.core.UpdateRelationshipCommand;
import com.dooall.mysocialnetwork.users.CreateUserResponse;
import com.dooall.mysocialnetwork.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileRegistrationService {

	private static final Logger log = LoggerFactory.getLogger(ProfileRegistrationService.class);

	private final UserService userService;
	private final ProfileRepository profileRepository;
	private final JdbcAggregateTemplate jdbcAggregateTemplate;
	private final RelationshipStorage relationshipStorage;

	public ProfileRegistrationService(UserService userService,
									  ProfileRepository profileRepository, JdbcAggregateTemplate jdbcAggregateTemplate, RelationshipStorage relationshipStorage) {
		this.userService = userService;
		this.profileRepository = profileRepository;
		this.jdbcAggregateTemplate = jdbcAggregateTemplate;
		this.relationshipStorage = relationshipStorage;
	}

	@Transactional
	public ProfileRegistrationResponse registerNewProfile(ProfileRegistrationRequest request) {
		log.info("Attempting to register profile for email: {}, handle: {}", request.email(), request.handle());

		// Use UserService to check for existing email
		if (this.userService.existsByEmail(request.email())) {
			log.warn("Registration failed: Email {} already exists", request.email());
			throw new IllegalArgumentException("Email already in use.");
		}

		if (this.profileRepository.existsByHandle(request.handle())) {
			log.warn("Registration failed: Handle {} already exists", request.handle());
			throw new IllegalArgumentException("Handle already in use.");
		}

		CreateUserResponse savedUser = this.userService.createUser(request.email(), request.password());

		Profile newProfile = new Profile(
				savedUser.id(), // The ID of the user associated with this profile
				request.displayName(),
				request.handle()
		);

		Profile savedProfile = this.jdbcAggregateTemplate.insert(newProfile);
		log.info("Profile created with ID: {} for user ID: {}", savedProfile.getId(), savedUser.id());

		setOwnerRelationship(savedProfile, savedUser);

		return new ProfileRegistrationResponse(
				savedUser.id(),
				savedProfile.getId(),
				savedUser.email(),
				savedProfile.getDisplayName(),
				savedProfile.getHandle()
		);
	}

	private void setOwnerRelationship(Profile savedProfile, CreateUserResponse savedUser) {
		UpdateRelationshipCommand command = UpdateRelationshipCommand.builder()
				.resourceName(ObjectTypes.profile)
				.resourceId(savedProfile.getId().toString())
				.relation("owner")
				.subjectName(ObjectTypes.user)
				.subjectId(savedUser.id().toString())
				.build();
		this.relationshipStorage.updateRelationship(command);
	}
}
