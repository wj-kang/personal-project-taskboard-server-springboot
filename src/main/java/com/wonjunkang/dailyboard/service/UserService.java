package com.wonjunkang.dailyboard.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wonjunkang.dailyboard.model.User;
import com.wonjunkang.dailyboard.persistence.UserRepository;

@Service
public class UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User save(User user) {
    User storedUser = userRepository.insert(user);
    return storedUser;
  }

  public Optional<User> findById(String id) {
    return userRepository.findById(id);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }
}
