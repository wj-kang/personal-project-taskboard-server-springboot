package com.wonjunkang.taskboard.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.wonjunkang.taskboard.dto.TaskListBaseDTO;
import com.wonjunkang.taskboard.dto.TaskOrderUpdateDTO;
import com.wonjunkang.taskboard.model.Board;
import com.wonjunkang.taskboard.model.Task;
import com.wonjunkang.taskboard.model.TaskList;
import com.wonjunkang.taskboard.persistence.BoardRepository;
import com.wonjunkang.taskboard.persistence.TaskListRepository;
import com.wonjunkang.taskboard.persistence.TaskRepository;

@RestController
@RequestMapping("/api/list")
public class TaskListController {
  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private TaskListRepository taskListRepository;

  @Autowired
  private TaskRepository taskRepository;

  @PostMapping
  public ResponseEntity<?> createTaskList(@AuthenticationPrincipal String userId,
      @RequestBody TaskList list) {
    try {
      list.setTitle("New List");
      list.setOwnerId(userId);
      list.setTasks(new ArrayList<>());
      TaskList newList = taskListRepository.save(list);

      Board board = boardRepository.findById(list.getBoardId()).get();
      if (!board.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
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

      if (list.getTitle() != null) {
        found.setTitle(list.getTitle());
      }
      if (list.getTasks() != null) {
        found.setTasks(list.getTasks());
      }
      TaskList updated = taskListRepository.save(found);

      if (list.getTasks() == null) {
        // title update case => return base DTO
        return ResponseEntity.status(200).body(new TaskListBaseDTO(updated));
      }
      return ResponseEntity.status(200).body(updated);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PutMapping("/task-drag")
  public ResponseEntity<?> updateTaskOrder(@AuthenticationPrincipal String userId,
      @RequestBody TaskOrderUpdateDTO data) {
    try {
      Board board = boardRepository.findById(data.getBoardId()).get();
      List<TaskList> lists = board.getLists();

      if (data.getDestListIdx() == data.getSrcListIdx()) {
        TaskList list = lists.get(data.getDestListIdx());
        List<Task> tasks = list.getTasks();
        Task taskToMove = tasks.get(data.getSrcTaskIdx());

        tasks.remove(taskToMove);
        tasks.add(data.getDestTaskIdx(), taskToMove);
        taskListRepository.save(list); // Update TaskList
      } else {
        TaskList srcList = lists.get(data.getSrcListIdx());
        TaskList destList = lists.get(data.getDestListIdx());

        Task taskToMove = srcList.getTasks().get(data.getSrcTaskIdx());
        taskToMove.setListId(destList.getId());
        taskRepository.save(taskToMove); // update task

        srcList.getTasks().remove(taskToMove);
        destList.getTasks().add(data.getDestTaskIdx(), taskToMove);

        taskListRepository.save(srcList); // update task-list
        taskListRepository.save(destList);
      }

      return ResponseEntity.status(200).body("ok");
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }
}
