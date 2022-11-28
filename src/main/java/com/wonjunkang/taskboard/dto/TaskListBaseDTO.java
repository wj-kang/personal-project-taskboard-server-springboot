package com.wonjunkang.taskboard.dto;

import com.wonjunkang.taskboard.model.TaskList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListBaseDTO {

  public TaskListBaseDTO(TaskList l) {
    this.title = l.getTitle();
    this.ownerId = l.getOwnerId();
    this.boardId = l.getBoardId();
  }

  private String title;

  private String ownerId;

  private String boardId;

}
