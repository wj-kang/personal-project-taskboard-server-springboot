package com.wonjunkang.taskboard.dto;

import com.wonjunkang.taskboard.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  public UserDTO(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.type = user.getType();
  }

  private String id;

  private String email;

  private String type;

  private String token;
}
