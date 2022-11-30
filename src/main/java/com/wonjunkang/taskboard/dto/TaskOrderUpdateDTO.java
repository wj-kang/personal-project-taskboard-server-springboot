package com.wonjunkang.taskboard.dto;

import lombok.Data;

@Data
public class TaskOrderUpdateDTO {

  private String boardId;

  private int srcListIdx;

  private int srcTaskIdx;

  private int destListIdx;

  private int destTaskIdx;

}
