package com.wonjunkang.dailyboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.dailyboard.model.Task;

public interface TaskRepository extends MongoRepository<Task, String> {

}
