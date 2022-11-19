package com.wonjunkang.taskboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.taskboard.model.Board;

public interface BoardRepository extends MongoRepository<Board, String> {
}
