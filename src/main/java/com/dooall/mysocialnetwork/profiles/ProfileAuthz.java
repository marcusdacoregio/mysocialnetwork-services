package com.dooall.mysocialnetwork.profiles;

import com.authzed.api.v1.CheckPermissionRequest;
import com.authzed.api.v1.CheckPermissionResponse;
import com.authzed.api.v1.ObjectReference;
import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SubjectReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProfileAuthz {

	private final PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

	ProfileAuthz(PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService) {
		this.permissionsService = permissionsService;
	}

	/**
	 * Checks whether the user has a relationship of owner with
	 * the profile
	 * @return
	 */
	public boolean isProfileOwner(UUID userId, UUID profileId) {
		CheckPermissionRequest request = CheckPermissionRequest.newBuilder()
				.setResource(
						ObjectReference.newBuilder()
								.setObjectType("profile")
								.setObjectId(profileId.toString())
								.build())
				.setSubject(
						SubjectReference.newBuilder()
								.setObject(
										ObjectReference.newBuilder()
												.setObjectType("user")
												.setObjectId(userId.toString())
												.build())
								.build())
				.setPermission("owner")
				.build();

		CheckPermissionResponse response = this.permissionsService.checkPermission(request);
		return response.getPermissionshipValue() == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION_VALUE;
	}

}
