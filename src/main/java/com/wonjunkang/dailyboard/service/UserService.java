package com.wonjunkang.dailyboard.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.wonjunkang.dailyboard.model.User;
import com.wonjunkang.dailyboard.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(User user) {
    if (user == null || user.getEmail() == null) {
      throw new RuntimeException("Invalid entity");
    }

    String email = user.getEmail();
    if (userRepository.existsByEmail(email)) {
      log.warn("Email already exists {}", email);
      throw new RuntimeException("Email already exists");
    }

    return userRepository.save(user);
  }

  public User getByCredentials(String email, String password, PasswordEncoder encoder) {
    User existUser = userRepository.findByEmail(email);

    if (existUser != null && encoder.matches(password, existUser.getPassword())) {
      return existUser;
    }
    return null;
  }

  public Optional<User> findById(String id) {
    return userRepository.findById(id);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }
}
