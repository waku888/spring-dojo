package com.example.blog.service.article;

import java.time.LocalDateTime;

public class ArticleService {
    public ArticleEntity findById(long id) {
        return new ArticleEntity(
                id,
                "This is title; id = " + id,
                "This is content",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
