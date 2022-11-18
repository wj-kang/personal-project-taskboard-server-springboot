package com.wonjunkang.dailyboard.controller;

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
import com.wonjunkang.dailyboard.dto.ResponseDTO;
import com.wonjunkang.dailyboard.dto.UserDTO;
import com.wonjunkang.dailyboard.model.User;
import com.wonjunkang.dailyboard.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @GetMapping
  public String testGetController() {
    return "User Controller";
  }

  @GetMapping("/{id}")
  public UserDTO getUserById(@PathVariable(required = false) String id) {
    Optional<User> found = userService.findById(id);

    if (found.isPresent()) {
      return new UserDTO(found.get());
    }
    return null;
  }

  @GetMapping("/all")
  public List<UserDTO> getAllUsers() {
    List<User> users = userService.findAll();
    return users.stream().map(UserDTO::new).toList();
  }

  @PostMapping("/register")
  public ResponseEntity<?> newUser(@RequestBody User user) {
    try {
      // encode input password
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      // save new enitty
      userService.createUser(user);
      // send response DTO
      return ResponseEntity.status(201).body("Created");
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody User user) {
    User foundUser =
        userService.getByCredentials(user.getEmail(), user.getPassword(), passwordEncoder);
    if (foundUser == null) {
      ResponseDTO<String> responseDTO =
          ResponseDTO.<String>builder().error("Invalid Credentials").build();
      return ResponseEntity.badRequest().body(responseDTO);
    }

    /* TODO : SIGN and ADD JWT TOKEN */
    UserDTO userDTO = new UserDTO(foundUser);
    userDTO.setToken("TODO : JWT");
    return ResponseEntity.ok().body(userDTO);
  }
}
