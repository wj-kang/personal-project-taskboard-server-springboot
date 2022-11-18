package com.wonjunkang.dailyboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.dailyboard.model.User;

public interface UserRepository extends MongoRepository<User, String> {
  User findByEmail(String email);

  Boolean existsByEmail(String email);
}
