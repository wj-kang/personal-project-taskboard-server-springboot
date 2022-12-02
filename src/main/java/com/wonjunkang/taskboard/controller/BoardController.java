package com.wonjunkang.taskboard.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wonjunkang.taskboard.dto.BoardOrderUpdateDTO;
import com.wonjunkang.taskboard.dto.ResponseDTO;
import com.wonjunkang.taskboard.dto.TaskListOrderUpdateDTO;
import com.wonjunkang.taskboard.model.Board;
import com.wonjunkang.taskboard.model.TaskList;
import com.wonjunkang.taskboard.model.User;
import com.wonjunkang.taskboard.persistence.BoardRepository;
import com.wonjunkang.taskboard.persistence.TaskListRepository;
import com.wonjunkang.taskboard.persistence.UserRepository;

@RestController
@RequestMapping("/api/board")
public class BoardController {

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TaskListRepository taskListRepository;

  @GetMapping("/{boardId}")
  public ResponseEntity<?> getBoardById(@AuthenticationPrincipal String userId,
      @PathVariable String boardId) {
    Optional<Board> found = boardRepository.findById(boardId);

    if (found.isPresent() && found.get().getOwnerId().equals(userId)) {
      return ResponseEntity.ok().body(found.get());
    }

    return ResponseEntity.badRequest().body("Invalid Request");
  }

  @GetMapping
  public ResponseEntity<?> getBoards(@AuthenticationPrincipal String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isPresent()) {
      return ResponseEntity.ok().body(user.get().getBoards());
    }
    return ResponseEntity.badRequest().body("Invalid Request");
  }

  @PostMapping
  public ResponseEntity<?> createBoard(@AuthenticationPrincipal String userId) {
    try {
      User user = userRepository.findById(userId).get();

      Board board = Board.builder()//
          .ownerId(userId)//
          .title("New Board")//
          .lists(new ArrayList<>()).build();
      Board newBoard = boardRepository.save(board);

      user.getBoards().add(newBoard);
      userRepository.save(user);

      TaskList newList = taskListRepository.save(TaskList.builder()//
          .title("New List")//
          .boardId(newBoard.getId())//
          .ownerId(user.getId())//
          .tasks(new ArrayList<>())//
          .build());
      newBoard.getLists().add(newList);
      newBoard = boardRepository.save(newBoard);

      return ResponseEntity.status(201).body(newBoard);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @DeleteMapping
  public ResponseEntity<?> deleteBoard(@AuthenticationPrincipal String userId,
      @RequestBody Board board) {
    try {
      if (board == null) {
        throw new RuntimeException("Invalid Request");
      }

      Board found = boardRepository.findById(board.getId()).get();
      if (!found.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }

      boardRepository.delete(found);
      return ResponseEntity.status(200).body("Deleted");
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PutMapping
  public ResponseEntity<?> updateBoard(@AuthenticationPrincipal String userId,
      @RequestBody Board board) {
    try {
      if (board.getId() == null) {
        throw new RuntimeException("Invalid Request");
      }

      Board found = boardRepository.findById(board.getId()).get();
      if (!found.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }

      if (board.getTitle() != null) {
        found.setTitle(board.getTitle());
      }
      if (board.getLists() != null) {
        found.setLists(board.getLists());
      }
      Board updated = boardRepository.save(found);
      return ResponseEntity.status(200).body(updated);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PutMapping("/board-drag")
  public ResponseEntity<?> updateBoardOrder(@AuthenticationPrincipal String userId,
      @RequestBody BoardOrderUpdateDTO data) {
    try {
      User user = userRepository.findById(userId).get();
      List<Board> boards = user.getBoards();
      reorderList(boards, data.getSrc(), data.getDest());
      userRepository.save(user);

      return ResponseEntity.status(200).body("ok");

    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PutMapping("/lists-order")
  public ResponseEntity<?> updateListsOrder(@AuthenticationPrincipal String userId,
      @RequestBody TaskListOrderUpdateDTO data) {
    try {
      Board board = boardRepository.findById(data.getBoardId()).get();
      if (!board.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }

      List<TaskList> lists = board.getLists();
      int currIdx = data.getIndex();
      int len = lists.size();
      boolean isLeft = data.isLeft();
      int destIdx;

      if (isLeft == true) {
        destIdx = (currIdx - 1 + len) % len;
      } else {
        destIdx = (currIdx + 1) % len;
      }

      reorderList(lists, currIdx, destIdx);
      boardRepository.save(board);

      return ResponseEntity.status(200).body("ok");

    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  /* Utility Methods */
  private <T> void reorderList(List<T> list, int src, int dest) {
    if (src < 0 || dest < 0 || src >= list.size() || dest >= list.size()) {
      throw new Error("Invalid Index");
    }

    if (src < dest) {
      for (int i = src; i < dest; i++) {
        swap(list, i, i + 1);
      }
    } else {
      for (int i = src; i > dest; i--) {
        swap(list, i, i - 1);
      }
    }
  }

  private <T> void swap(List<T> list, int idx1, int idx2) {
    T temp = list.get(idx1);
    list.set(idx1, list.get(idx2));
    list.set(idx2, temp);
  }
}
