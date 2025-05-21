package com.dooall.mysocialnetwork.authz;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.grpcutil.BearerToken;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpiceDbConfig {

	@Value("${spicedb.host}")
	private String host;

	@Value("${spicedb.grpc-port}")
	private int port;

	@Bean
	PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionService() {
		ManagedChannel channel = ManagedChannelBuilder
				.forTarget(this.host + ":" + this.port)
				.usePlaintext()
				.build();
		BearerToken bearerToken = new BearerToken("somerandomkey");
		return PermissionsServiceGrpc
				.newBlockingStub(channel)
				.withCallCredentials(bearerToken);
	}

}
