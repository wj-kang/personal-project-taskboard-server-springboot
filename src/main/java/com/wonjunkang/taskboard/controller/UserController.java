package com.wonjunkang.taskboard.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wonjunkang.taskboard.dto.ResponseDTO;
import com.wonjunkang.taskboard.dto.UserBaseDTO;
import com.wonjunkang.taskboard.dto.UserLoginDTO;
import com.wonjunkang.taskboard.model.Board;
import com.wonjunkang.taskboard.model.TaskList;
import com.wonjunkang.taskboard.model.User;
import com.wonjunkang.taskboard.persistence.BoardRepository;
import com.wonjunkang.taskboard.persistence.TaskListRepository;
import com.wonjunkang.taskboard.persistence.UserRepository;
import com.wonjunkang.taskboard.security.TokenProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private TaskListRepository taskListRepository;

  @Autowired
  private TokenProvider tokenProvider;

  private final PasswordEncoder encoder = new BCryptPasswordEncoder();

  @GetMapping
  public ResponseEntity<?> getUser(@AuthenticationPrincipal String userId) {
    try {
      User user = userRepository.findById(userId).get();
      UserLoginDTO userLoginDTO = new UserLoginDTO(user, "");

      return ResponseEntity.ok().body(userLoginDTO);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable(required = false) String id) {
    Optional<User> found = userRepository.findById(id);

    if (found.isPresent()) {
      return ResponseEntity.ok().body(new UserBaseDTO(found.get()));
    }
    return ResponseEntity.badRequest().body("Invalid Record");
  }

  @GetMapping("/all")
  public List<UserBaseDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream().map(UserBaseDTO::new).toList();
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
      user.setBoards(new ArrayList<>());
      User createdUser = userRepository.save(user);

      // Create default board and task-list for this new user
      Board newBoard = boardRepository.save(Board.builder().title("New Board")
          .ownerId(createdUser.getId()).lists(new ArrayList<>()).build());
      createdUser.getBoards().add(newBoard);
      userRepository.save(createdUser);

      // TaskList newList = taskListRepository.save(TaskList.builder().title("New List")
      // .boardId(newBoard.getId()).ownerId(createdUser.getId()).build());
      // newBoard.getLists().add(newList);
      // boardRepository.save(newBoard);
      createDefaultTaskListsForNewUser(newBoard);

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

      String token = tokenProvider.createToken(existUser.getId());
      UserLoginDTO userLoginDTO = new UserLoginDTO(existUser, token);

      return ResponseEntity.ok().body(userLoginDTO);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @GetMapping("/guest")
  public ResponseEntity<?> guestEnter() {
    try {
      User user = User.builder()//
          .email(UUID.randomUUID().toString().substring(0, 12) + "@taskboard.guest")//
          .password(UUID.randomUUID().toString())//
          .type("GUEST").boards(new ArrayList<>()).build();

      User createdUser = userRepository.save(user);

      // Create default board and task-list for this new user
      Board newBoard = boardRepository.save(Board.builder().title("New Board")
          .ownerId(createdUser.getId()).lists(new ArrayList<>()).build());
      createdUser.getBoards().add(newBoard);
      userRepository.save(createdUser);

      // TaskList newList = taskListRepository.save(TaskList.builder().title("New List")
      // .boardId(newBoard.getId()).ownerId(createdUser.getId()).build());
      // newBoard.getLists().add(newList);
      // boardRepository.save(newBoard);
      createDefaultTaskListsForNewUser(newBoard);

      String token = tokenProvider.createToken(createdUser.getId());
      UserLoginDTO userLoginDTO = new UserLoginDTO(createdUser, token);

      return ResponseEntity.ok().body(userLoginDTO);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  private Board createDefaultTaskListsForNewUser(Board board) {
    String[] titles = new String[] {"To do", "Doing", "Done"};
    for (String title : titles) {
      TaskList newList = taskListRepository.save(TaskList.builder().title(title)
          .boardId(board.getId()).ownerId(board.getOwnerId()).tasks(new ArrayList<>()).build());
      board.getLists().add(newList);
    }

    return boardRepository.save(board);
  }
}
