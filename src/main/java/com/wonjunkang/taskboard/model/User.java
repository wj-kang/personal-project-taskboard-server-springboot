package com.wonjunkang.taskboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {

  @Indexed(unique = true)
  private String email;

  private String password;

  private String type;

}
