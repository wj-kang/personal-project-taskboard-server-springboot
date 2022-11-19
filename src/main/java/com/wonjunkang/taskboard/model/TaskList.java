package com.wonjunkang.taskboard.model;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("task-lists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskList extends BaseModel {

  private String title;

  @DBRef
  private List<Task> tasks;

}

