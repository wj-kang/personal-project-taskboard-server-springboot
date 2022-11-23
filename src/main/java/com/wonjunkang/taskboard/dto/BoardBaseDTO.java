package com.wonjunkang.taskboard.dto;

import com.wonjunkang.taskboard.model.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardBaseDTO {

  public BoardBaseDTO(Board board) {
    this.id = board.getId();
    this.title = board.getTitle();
  }

  private String id;

  private String title;

}
