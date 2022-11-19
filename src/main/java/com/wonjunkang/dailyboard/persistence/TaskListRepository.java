package com.wonjunkang.dailyboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.dailyboard.model.TaskList;

public interface TaskListRepository extends MongoRepository<TaskList, String> {
}
