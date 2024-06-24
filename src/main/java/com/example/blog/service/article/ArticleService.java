package com.example.blog.service.article;

import java.time.LocalDateTime;
import java.util.Optional;

public class ArticleService {
    public Optional<ArticleEntity> findById(long id) {
        return Optional.of(new ArticleEntity(
                id,
                "This is title; id = " + id,
                "This is content",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
    }
}
