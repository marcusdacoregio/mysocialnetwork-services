package com.dooall.mysocialnetwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	private static final Logger log = LoggerFactory.getLogger(TestcontainersConfiguration.class);

	static Network network = Network.builder().driver("bridge").build();

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.13-alpine3.20"))
			.withDatabaseName("mysocialnetwork")
			.withUsername("postgres")
			.withPassword("secret")
			.withNetworkAliases("postgres")
			.withNetwork(network);

	static GenericContainer<?> spiceDbMigrate = new GenericContainer<>(DockerImageName.parse("quay.io/authzed/spicedb:v1.43.0-debug"))
			.withCommand("migrate head")
			.withEnv("SPICEDB_GRPC_PRESHARED_KEY", "somerandomkey")
			.withEnv("SPICEDB_LOG_LEVEL", "debug")
			.withEnv("SPICEDB_DATASTORE_ENGINE", "postgres")
			.withEnv("SPICEDB_DATASTORE_CONN_URI", "postgres://postgres:secret@postgres:5432/mysocialnetwork?sslmode=disable")
			.withNetworkAliases("spicedb-migrate")
			.withNetwork(network)
			.dependsOn(postgres)
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("SPICEDB-MIGRATE"));

	static GenericContainer<?> spiceDbEngine = new GenericContainer<>(DockerImageName.parse("quay.io/authzed/spicedb:v1.43.0-debug"))
			.withExposedPorts(50051)
			.withCommand("serve")
			.withEnv("SPICEDB_GRPC_PRESHARED_KEY", "somerandomkey")
			.withEnv("SPICEDB_LOG_LEVEL", "debug")
			.withEnv("SPICEDB_DATASTORE_ENGINE", "postgres")
			.withEnv("SPICEDB_HTTP_ENABLED", "true")
			.withEnv("SPICEDB_DATASTORE_CONN_URI", "postgres://postgres:secret@postgres:5432/mysocialnetwork?sslmode=disable")
			.withNetworkAliases("spicedb-engine")
			.withNetwork(network)
			.dependsOn(postgres, spiceDbMigrate)
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("SPICEDB-ENGINE"));

	static GenericContainer<?> spiceDbSchemaLoader = new GenericContainer<>(DockerImageName.parse("quay.io/authzed/zed:v0.13.0"))
			.withFileSystemBind("./config/spicedb/schema.zed", "/schema/schema.zed", BindMode.READ_ONLY)
			.withEnv("ZED_ENDPOINT", "spicedb-engine:50051")
			.withEnv("ZED_TOKEN", "somerandomkey")
			.withEnv("SPICEDB_INSECURE", "true")
			.withCommand("schema", "write", "/schema/schema.zed", "--insecure")
			.dependsOn(spiceDbEngine)
			.withNetwork(network)
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("SPICEDB-MIGRATE"));

	static {
		spiceDbEngine.setPortBindings(List.of("50051:50051"));
		postgres.start();
		spiceDbEngine.start();
		spiceDbSchemaLoader.start();
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgreSQLContainer() {
		return postgres;
	}

	@DynamicPropertySource
	static void containerProperties(DynamicPropertyRegistry registry) {
		registry.add("spicedb.host", spiceDbEngine::getHost);
		registry.add("spicedb.grpc-port", () -> spiceDbEngine.getMappedPort(50051));
	}

}
