package com.dooall.mysocialnetwork.profiles;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, UUID> {
	boolean existsByHandle(String handle);
}
