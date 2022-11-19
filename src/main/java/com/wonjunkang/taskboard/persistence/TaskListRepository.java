package com.wonjunkang.taskboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.taskboard.model.TaskList;

public interface TaskListRepository extends MongoRepository<TaskList, String> {
}
