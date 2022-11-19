package com.wonjunkang.dailyboard.model;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseModel {

  private String title;

  private String description;

  private boolean isDone;

  private Date dueDate;

  private List<String> label;

}

