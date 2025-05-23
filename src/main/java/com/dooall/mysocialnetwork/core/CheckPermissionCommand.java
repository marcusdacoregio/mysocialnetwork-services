package com.dooall.mysocialnetwork.core;

public record CheckPermissionCommand(ObjectTypes resourceName, String resourceId,
									 String permission, ObjectTypes subjectName, String subjectId) {

	public CheckPermissionCommand {
		if (resourceName == null || resourceId == null || permission == null || subjectName == null || subjectId == null) {
			throw new IllegalArgumentException("All fields must be non-null");
		}
	}

	public static UpdateRelationshipCommandBuilder builder() {
		return new UpdateRelationshipCommandBuilder();
	}

	public static class UpdateRelationshipCommandBuilder {
		private ObjectTypes resourceName;
		private String resourceId;
		private String permission;
		private ObjectTypes subjectName;
		private String subjectId;

		public UpdateRelationshipCommandBuilder resourceName(ObjectTypes resourceName) {
			this.resourceName = resourceName;
			return this;
		}

		public UpdateRelationshipCommandBuilder resourceId(String resourceId) {
			this.resourceId = resourceId;
			return this;
		}

		public UpdateRelationshipCommandBuilder permission(String permission) {
			this.permission = permission;
			return this;
		}

		public UpdateRelationshipCommandBuilder subjectName(ObjectTypes subjectName) {
			this.subjectName = subjectName;
			return this;
		}

		public UpdateRelationshipCommandBuilder subjectId(String subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public CheckPermissionCommand build() {
			return new CheckPermissionCommand(this.resourceName, this.resourceId, this.permission, this.subjectName, this.subjectId);
		}
	}
}
