package com.wonjunkang.taskboard.controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wonjunkang.taskboard.dto.ResponseDTO;
import com.wonjunkang.taskboard.model.Board;
import com.wonjunkang.taskboard.model.TaskList;
import com.wonjunkang.taskboard.persistence.BoardRepository;
import com.wonjunkang.taskboard.persistence.TaskListRepository;

@RestController
@RequestMapping("/api/list")
public class TaskListController {
  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private TaskListRepository taskListRepository;

  @PostMapping
  public ResponseEntity<?> createTaskList(@AuthenticationPrincipal String userId,
      @RequestBody TaskList list) {
    try {
      list.setTitle("New List");
      list.setOwnerId(userId);
      TaskList newList = taskListRepository.save(list);

      Board board = boardRepository.findById(list.getBoardId()).get();
      if (board.getLists() == null) {
        board.setLists(new ArrayList<TaskList>());
      }
      board.getLists().add(newList);
      boardRepository.save(board);

      return ResponseEntity.status(201).body(newList);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @DeleteMapping
  public ResponseEntity<?> deleteTaskList(@AuthenticationPrincipal String userId,
      @RequestBody TaskList list) {
    try {
      if (list == null) {
        throw new RuntimeException("Invalid Request");
      }

      TaskList found = taskListRepository.findById(list.getId()).get();
      if (!found.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }

      taskListRepository.delete(found);
      return ResponseEntity.status(200).body("Deleted");
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PutMapping
  public ResponseEntity<?> updateTaskList(@AuthenticationPrincipal String userId,
      @RequestBody TaskList list) {
    try {
      if (list.getId() == null) {
        throw new RuntimeException("Invalid Request");
      }

      TaskList found = taskListRepository.findById(list.getId()).get();
      if (!found.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }

      found.setTitle(list.getTitle());
      TaskList updated = taskListRepository.save(found);

      return ResponseEntity.status(200).body(updated);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }
}
