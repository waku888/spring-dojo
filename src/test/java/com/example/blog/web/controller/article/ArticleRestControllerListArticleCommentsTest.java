package com.example.blog.web.controller.article;

import com.example.blog.security.LoggedInUser;
import com.example.blog.service.article.ArticleCommentEntity;
import com.example.blog.service.article.ArticleCommentService;
import com.example.blog.service.article.ArticleEntity;
import com.example.blog.service.article.ArticleService;
import com.example.blog.service.user.UserEntity;
import com.example.blog.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleRestControllerListArticleCommentsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleCommentService articleCommentService;
    private ArticleCommentEntity comment1;
    private ArticleCommentEntity comment2;

    private ArticleEntity article;
    private UserEntity commentAuthor;
    private LoggedInUser loggedInCommentAuthor;
    @BeforeEach
    void beforeEach() {
        var articleAuthor = userService.register("test_username1", "test_password1");
        article = articleService.create(
                articleAuthor.getId(),
                "test_article_title",
                "test_article_body"
        );
        var commentAuthor1 = userService.register("test_username2", "test_password2");
        var commentAuthor2 = userService.register("test_username3", "test_password3");
        comment1 = articleCommentService.create(commentAuthor1.getId(), article.getId(), "test_comment_body1");
        comment2 = articleCommentService.create(commentAuthor2.getId(), article.getId(), "test_comment_body2");
    }

    @Test
    void setup() {
        // ## Arrange ##

        // ## Act ##

        // ## Assert ##
        assertThat(mockMvc).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    @DisplayName("GET /articles/{articleId}/comments: 指定した記事のコメント一覧を取得できる")
    void listArticleComments_200OK() throws Exception {
        // ## Arrange ##
        // ## Act ##
        var actual = mockMvc.perform(
                get("/articles/{articleId}/comments", article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // ## Assert ##
        actual
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.comments[0].id").value(comment1.getId()))
                .andExpect(jsonPath("$.comments[0].body").value(comment1.getBody()))
                .andExpect(jsonPath("$.comments[0].author.id").value(comment1.getAuthor().getId()))
                .andExpect(jsonPath("$.comments[0].author.username").value(comment1.getAuthor().getUsername()))
                .andExpect(jsonPath("$.comments[0].createdAt").value(comment1.getCreatedAt().toString()))
                .andExpect(jsonPath("$.comments[1].id").value(comment2.getId()))
                .andExpect(jsonPath("$.comments[1].body").value(comment2.getBody()))
                .andExpect(jsonPath("$.comments[1].author.id").value(comment2.getAuthor().getId()))
                .andExpect(jsonPath("$.comments[1].author.username").value(comment2.getAuthor().getUsername()))
                .andExpect(jsonPath("$.comments[1].createdAt").value(comment2.getCreatedAt().toString()))
        ;
    }
        ;
    }


}