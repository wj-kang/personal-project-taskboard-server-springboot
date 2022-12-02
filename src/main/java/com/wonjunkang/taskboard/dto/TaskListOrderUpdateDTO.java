package com.wonjunkang.taskboard.dto;

import lombok.Data;

@Data
public class TaskListOrderUpdateDTO {

  private String boardId;

  private int index;

  private boolean left;

}
