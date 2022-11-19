package com.wonjunkang.taskboard.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wonjunkang.taskboard.dto.ResponseDTO;
import com.wonjunkang.taskboard.dto.UserDTO;
import com.wonjunkang.taskboard.model.User;
import com.wonjunkang.taskboard.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserRepository userRepository;

  private final PasswordEncoder encoder;

  @Autowired
  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.encoder = new BCryptPasswordEncoder();
  }

  @GetMapping
  public String testGetController() {
    return "User Controller";
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable(required = false) String id) {
    Optional<User> found = userRepository.findById(id);

    if (found.isPresent()) {
      return ResponseEntity.ok().body(new UserDTO(found.get()));
    }
    return ResponseEntity.badRequest().body("Invalid Record");
  }

  @GetMapping("/all")
  public List<UserDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream().map(UserDTO::new).toList();
  }

  @PostMapping("/register")
  public ResponseEntity<?> newUser(@RequestBody User user) {
    try {
      if (user == null || user.getEmail() == null) {
        throw new RuntimeException("Invalid entity");
      }

      if (userRepository.existsByEmail(user.getEmail())) {
        log.warn("Email already exists {}", user.getEmail());
        throw new RuntimeException("Email already exists");
      }

      user.setPassword(encoder.encode(user.getPassword()));
      user.setType("SIGN_UP");
      userRepository.save(user);
      return ResponseEntity.status(201).body("New User Created");
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody User user) {
    try {
      User existUser = userRepository.findByEmail(user.getEmail());
      if (existUser == null || !encoder.matches(user.getPassword(), existUser.getPassword())) {
        throw new RuntimeException("Invalid Credentials");
      }

      /* TODO : SIGN and ADD JWT TOKEN */
      UserDTO userDTO = new UserDTO(existUser);
      userDTO.setToken("TODO : JWT");

      return ResponseEntity.ok().body(userDTO);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }

  }
}
