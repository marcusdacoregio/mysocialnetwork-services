package com.dooall.mysocialnetwork;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.test.context.jdbc.Sql;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Sql(scripts = "classpath:cleanup_database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public @interface CleanupDatabase {}
