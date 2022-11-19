package com.wonjunkang.taskboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.taskboard.model.Task;

public interface TaskRepository extends MongoRepository<Task, String> {

}
