package com.wonjunkang.dailyboard.mongo;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import com.wonjunkang.dailyboard.model.BaseModel;
import java.util.Date;

@Component
public class MongoListener extends AbstractMongoEventListener<BaseModel> {
  @Override
  public void onBeforeConvert(BeforeConvertEvent<BaseModel> event) {
    super.onBeforeConvert(event);

    Date now = new Date();
    if (event.getSource().getCreatedAt() == null) {
      event.getSource().setCreatedAt(now);
    }
    event.getSource().setUpdatedAt(now);
  }
}
