package com.example.blog.repository.article;

import com.example.blog.service.article.ArticleEntity;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface ArticleRepository {

    @Select("""
            SELECT
                id
              , title
              , body
              , created_at
              , updated_at
            FROM articles
            WHERE id = #{id}
            """)
    Optional<ArticleEntity> selectById(@Param("id") long id);

    @Insert("""
            INSERT INTO articles (title, body, user_id, created_at, updated_at)
            VALUES (#{title}, #{body}, #{author.id}, #{createdAt},
            #{updatedAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(ArticleEntity entity);
}
