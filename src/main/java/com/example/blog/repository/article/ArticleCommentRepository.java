package com.example.blog.repository.article;

import com.example.blog.service.article.ArticleCommentEntity;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface ArticleCommentRepository {

    @Insert("""
            INSERT INTO article_comments(body, user_id, article_id, created_at)
            VALUES (#{body}, #{author.id}, #{article.id}, #{createdAt});
            """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insert(ArticleCommentEntity entity);
    @Select("""
            SELECT
              ac.id AS article_comment__id
            , ac.body AS article_comment__body
            , ac.created_at AS article_comment__created_at
            , a.id AS article__id
            , a.title AS article__title
            , a.body AS article__body
            , a.created_at AS article__created_at
            , a.updated_at AS article__updated_at
            , u.id AS user__id
            , u.username AS user__username
            , u.enabled AS user__enabled
            FROM article_comments ac
            JOIN articles a ON ac.article_id = a.id
            JOIN users u ON ac.user_id = u.id
            WHERE ac.id = #{id}
            ORDER BY ac.created_at ASC
            """)
    @Results(value = {
            @Result(column = "article_comment__id", property = "id"),
            @Result(column = "article_comment__body", property = "body"),
            @Result(column = "article_comment__created_at", property = "createdAt"),
            @Result(column = "article__id", property = "article.id"),
            @Result(column = "article__title", property = "article.title"),
            @Result(column = "article__body", property = "article.body"),
            @Result(column = "article__created_at", property = "article.createdAt"),
            @Result(column = "article__updated_at", property = "article.updatedAt"),
            @Result(column = "user__id", property = "author.id"),
            @Result(column = "user__username", property = "author.username"),
            @Result(column = "user__enabled", property = "author.enabled"),
    })
    Optional<ArticleCommentEntity> selectById(long articleCommentId);
}
