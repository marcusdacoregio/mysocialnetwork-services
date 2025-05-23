package com.dooall.mysocialnetwork.core;

public record UpdateRelationshipCommand(ObjectTypes resourceName, String resourceId,
		String relation, ObjectTypes subjectName, String subjectId) {

	public UpdateRelationshipCommand {
		if (resourceName == null || resourceId == null || relation == null || subjectName == null || subjectId == null) {
			throw new IllegalArgumentException("All fields must be non-null");
		}
	}

	public static UpdateRelationshipCommandBuilder builder() {
		return new UpdateRelationshipCommandBuilder();
	}

	public static class UpdateRelationshipCommandBuilder {
		private ObjectTypes resourceName;
		private String resourceId;
		private String relation;
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

		public UpdateRelationshipCommandBuilder relation(String relation) {
			this.relation = relation;
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

		public UpdateRelationshipCommand build() {
			return new UpdateRelationshipCommand(this.resourceName, this.resourceId, this.relation, this.subjectName, this.subjectId);
		}
	}
}
