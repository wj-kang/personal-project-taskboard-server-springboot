package com.wonjunkang.taskboard.dto;

import java.util.List;
import java.util.ArrayList;
import com.wonjunkang.taskboard.model.Board;
import com.wonjunkang.taskboard.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO extends UserBaseDTO {

  public UserLoginDTO(User user, String token) {
    super(user);
    this.boards = getBoardDTOList(user.getBoards());
    this.token = token;
  }

  private List<BoardBaseDTO> boards;

  private String token;

  private List<BoardBaseDTO> getBoardDTOList(List<Board> boards) {
    return boards.stream().map(BoardBaseDTO::new).toList();
  }

}
