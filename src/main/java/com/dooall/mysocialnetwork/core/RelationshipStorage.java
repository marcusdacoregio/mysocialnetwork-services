package com.dooall.mysocialnetwork.core;

import com.authzed.api.v1.CheckPermissionRequest;
import com.authzed.api.v1.CheckPermissionResponse;
import com.authzed.api.v1.Consistency;
import com.authzed.api.v1.ObjectReference;
import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.Relationship;
import com.authzed.api.v1.RelationshipUpdate;
import com.authzed.api.v1.SubjectReference;
import com.authzed.api.v1.WriteRelationshipsRequest;
import com.authzed.api.v1.WriteRelationshipsResponse;
import com.authzed.api.v1.ZedToken;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RelationshipStorage {

	private final AtomicReference<ZedToken> zedTokenRef = new AtomicReference<>();

	private final PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

	public RelationshipStorage(PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService) {
		this.permissionsService = permissionsService;
	}

	public void updateRelationship(UpdateRelationshipCommand command) {
		WriteRelationshipsRequest relationship = WriteRelationshipsRequest.newBuilder()
				.addUpdates(
						RelationshipUpdate.newBuilder()
								.setOperation(RelationshipUpdate.Operation.OPERATION_CREATE)
								.setRelationship(
										Relationship.newBuilder()
												.setResource(
														ObjectReference.newBuilder()
																.setObjectType(command.resourceName().name())
																.setObjectId(command.resourceId())
																.build())
												.setRelation(command.relation())
												.setSubject(
														SubjectReference.newBuilder()
																.setObject(
																		ObjectReference.newBuilder()
																				.setObjectType(command.subjectName().name())
																				.setObjectId(command.subjectId())
																				.build())
																.build())
												.build())
								.build())
				.build();

		WriteRelationshipsResponse writeRelationshipsResponse = this.permissionsService.writeRelationships(relationship);
		this.zedTokenRef.set(writeRelationshipsResponse.getWrittenAt());
	}

	public boolean checkRelationship(CheckPermissionCommand command) {
		CheckPermissionRequest.Builder requestBuilder = CheckPermissionRequest.newBuilder();

		ZedToken currentToken = this.zedTokenRef.get();
		if (currentToken != null) {
			requestBuilder.setConsistency(
					Consistency.newBuilder()
							.setAtLeastAsFresh(currentToken)
							.build());
		}

		requestBuilder.setResource(
				ObjectReference.newBuilder()
						.setObjectType(command.resourceName().name())
						.setObjectId(command.resourceId())
						.build())
				.setSubject(
						SubjectReference.newBuilder()
								.setObject(
										ObjectReference.newBuilder()
												.setObjectType(command.subjectName().name())
												.setObjectId(command.subjectId())
												.build())
								.build())
				.setPermission(command.permission());

		CheckPermissionResponse response = this.permissionsService.checkPermission(requestBuilder.build());
		return response.getPermissionshipValue() == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION_VALUE;
	}

}
