package com.dooall.mysocialnetwork.profiles;

import com.dooall.mysocialnetwork.core.CheckPermissionCommand;
import com.dooall.mysocialnetwork.core.ObjectTypes;
import com.dooall.mysocialnetwork.core.RelationshipStorage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProfileAuthz {

	private final RelationshipStorage relationshipStorage;

	public ProfileAuthz(RelationshipStorage relationshipStorage) {
		this.relationshipStorage = relationshipStorage;
	}

	/**
	 * Checks whether the user has a relationship of owner with
	 * the profile
	 * @return
	 */
	public boolean canEdit(UUID userId, UUID profileId) {
		CheckPermissionCommand command = CheckPermissionCommand.builder()
				.resourceName(ObjectTypes.profile)
				.resourceId(profileId.toString())
				.permission("edit")
				.subjectName(ObjectTypes.user)
				.subjectId(userId.toString())
				.build();
		return this.relationshipStorage.checkRelationship(command);
	}

}
