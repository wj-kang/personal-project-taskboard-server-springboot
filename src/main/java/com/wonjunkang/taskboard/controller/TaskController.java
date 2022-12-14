package com.wonjunkang.taskboard.controller;

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
import com.wonjunkang.taskboard.model.Task;
import com.wonjunkang.taskboard.model.TaskList;
import com.wonjunkang.taskboard.persistence.TaskListRepository;
import com.wonjunkang.taskboard.persistence.TaskRepository;

@RestController
@RequestMapping("/api/task")
public class TaskController {
  @Autowired
  private TaskListRepository taskListRepository;

  @Autowired
  private TaskRepository taskRepository;

  @PostMapping
  public ResponseEntity<?> createTask(@AuthenticationPrincipal String userId,
      @RequestBody Task task) {
    try {
      task.setOwnerId(userId);
      if (task.getTitle() == null || task.getTitle() == "") {
        task.setTitle("New task");
      }
      Task newTask = taskRepository.save(task);

      TaskList list = taskListRepository.findById(task.getListId()).get();
      if (!list.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }
      list.getTasks().add(newTask);
      taskListRepository.save(list);

      return ResponseEntity.status(201).body(newTask);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @DeleteMapping
  public ResponseEntity<?> deleteTask(@AuthenticationPrincipal String userId,
      @RequestBody Task task) {
    try {
      if (task == null) {
        throw new RuntimeException("Invalid Request");
      }

      Task found = taskRepository.findById(task.getId()).get();
      if (!found.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }

      TaskList list = taskListRepository.findById(found.getListId()).get();
      list.getTasks().remove(found);
      taskListRepository.save(list);

      taskRepository.delete(found);
      return ResponseEntity.status(200).body("Deleted");
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }

  @PutMapping
  public ResponseEntity<?> updateTask(@AuthenticationPrincipal String userId,
      @RequestBody Task task) {
    try {
      if (task.getId() == null) {
        throw new RuntimeException("Invalid Request");
      }

      Task found = taskRepository.findById(task.getId()).get();
      if (!found.getOwnerId().equals(userId)) {
        throw new RuntimeException("Invalid Request");
      }

      if (task.getTitle() != null) {
        found.setTitle(task.getTitle());
      }
      if (task.getDescription() != null) {
        found.setDescription(task.getDescription());
      }
      if (task.getDueDate() != null) {
        found.setDueDate(task.getDueDate());
      }
      if (task.getLabel() != null) {
        found.setLabel(task.getLabel());
      }
      Task updated = taskRepository.save(found);

      return ResponseEntity.status(200).body(updated);
      //
    } catch (Exception e) {
      ResponseDTO<String> responseDTO = ResponseDTO.<String>builder().error(e.getMessage()).build();
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }
}
