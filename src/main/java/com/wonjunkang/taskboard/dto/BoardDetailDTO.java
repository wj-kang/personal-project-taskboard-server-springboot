package com.wonjunkang.taskboard.dto;

import java.util.List;
import com.wonjunkang.taskboard.model.Board;
import com.wonjunkang.taskboard.model.TaskList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailDTO extends BoardBaseDTO {

  public BoardDetailDTO(Board board) {
    super(board);
    this.lists = board.getLists();
  }

  private List<TaskList> lists;

}
