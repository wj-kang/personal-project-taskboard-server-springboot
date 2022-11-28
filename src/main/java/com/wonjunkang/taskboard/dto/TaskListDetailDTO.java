package com.wonjunkang.taskboard.dto;

import java.util.List;
import com.wonjunkang.taskboard.model.Task;
import com.wonjunkang.taskboard.model.TaskList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDetailDTO extends TaskListBaseDTO {

  public TaskListDetailDTO(TaskList l) {
    super(l.getTitle(), l.getOwnerId(), l.getBoardId());
    this.tasks = l.getTasks();
  }

  private List<Task> tasks;

}
