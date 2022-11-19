package com.wonjunkang.taskboard.model;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("boards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseModel {

  private String title;

  @DBRef
  private List<TaskList> lists;

}


