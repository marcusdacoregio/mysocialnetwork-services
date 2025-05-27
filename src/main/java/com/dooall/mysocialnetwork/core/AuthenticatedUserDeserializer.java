package com.dooall.mysocialnetwork.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

class AuthenticatedUserDeserializer extends JsonDeserializer<AuthenticatedUser> {

	private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET =
			new TypeReference<>() {};

	@Override
	public AuthenticatedUser deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		JsonNode jsonNode = mapper.readTree(jp);
		Set<? extends GrantedAuthority> authorities =
				mapper.convertValue(jsonNode.get("authorities"), SIMPLE_GRANTED_AUTHORITY_SET);
		JsonNode passwordNode = readJsonNode(jsonNode, "password");
		UUID userId = UUID.fromString(readJsonNode(jsonNode, "userId").asText());
		UUID profileId = UUID.fromString(readJsonNode(jsonNode, "profileId").asText());
		String email = readJsonNode(jsonNode, "email").asText();
		String password = passwordNode.asText("");
		AuthenticatedUser result = new AuthenticatedUser(email, password);
		result.setProfileId(profileId);
		result.setUserId(userId);
		return result;
	}

	private JsonNode readJsonNode(JsonNode jsonNode, String field) {
		return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
	}
}
