package com.wonjunkang.taskboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.taskboard.model.User;

public interface UserRepository extends MongoRepository<User, String> {
  User findByEmail(String email);

  Boolean existsByEmail(String email);
}
