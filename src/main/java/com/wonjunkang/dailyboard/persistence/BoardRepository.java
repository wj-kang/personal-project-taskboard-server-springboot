package com.wonjunkang.dailyboard.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.wonjunkang.dailyboard.model.Board;

public interface BoardRepository extends MongoRepository<Board, String> {
}
