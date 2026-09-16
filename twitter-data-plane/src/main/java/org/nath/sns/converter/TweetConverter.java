package org.nath.sns.converter;

import org.bson.Document;
import org.nath.sns.entity.TweetEntity;

public class TweetConverter {

    public static Document toDocument(TweetEntity entity) {
        return new Document()
                .append("content", entity.getContent())
                .append("authorId", entity.getAuthorId())
                .append("createAt", entity.getCreateAt())
                .append("updateAt", entity.getUpdateAt());
    }

    public static TweetEntity toEntity(Document document) {
        return TweetEntity.builder()
                .id(document.getObjectId("_id").toString())
                .content(document.getString("content"))
                .authorId(document.getLong("authorId"))
                .createAt(document.getLong("createAt"))
                .updateAt(document.getLong("updateAt"))
                .build();
    }
}
