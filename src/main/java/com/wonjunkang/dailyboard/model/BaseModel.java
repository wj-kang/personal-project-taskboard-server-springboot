package com.wonjunkang.dailyboard.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import lombok.Data;

@Data
public class BaseModel {
  @Id
  private String id;

  private Date createdAt;

  private Date updatedAt;
}
