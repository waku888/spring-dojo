package com.example.blog.web.controller.article;

import com.example.blog.security.LoggedInUser;
import com.example.blog.service.article.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
class ArticleRestController500InternalServerErrorTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ArticleService articleService;

    @Test
    @DisplayName("MockMvc")
    void setUp_success() {
        // ## Arrange ##
        
        // ## Act ##

        // ## Assert ##
        assertThat(mockMvc).isNotNull();
        assertThat(articleService).isNotNull();
    }

    @Test
    @DisplayName("DisplayName(\"POST /users: 500 InternalServerError のとき、スタックトレースがレスポンスに露出しない")
    void createArticle_500_internalServerError() throws Exception {
        // ## Arrange ##
        var userId = 999L;
        var title = "test_title";
        var body = "test_body";
        when(articleService.create(userId, title, body)).thenThrow(RuntimeException.class);
        var bodyJson = """
                    {
                    "username": "%s",
                    "password": "%s"
                    }
                    """.formatted(title, body);

        // ## Act ##
        var actual =mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(new LoggedInUser(userId, "test_username","", true)))
                .content(bodyJson));

        // ## Assert ##
        actual
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").isEmpty())
                .andExpect(jsonPath("$.instance").value("/articles"))
                .andExpect(jsonPath("$", aMapWithSize(4)))
        ;
    }

    @Test
    @DisplayName("GET /articles: 500 InternalServerError でstacktrace が露出しない")
    void listArticles_500() throws Exception {
        // ## Arrange ##
        when(articleService.findAll()).thenThrow(RuntimeException.class);
        // ## Act ##
        var actual = mockMvc.perform(
                get("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // ## Assert ##
        actual
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").isEmpty())
                .andExpect(jsonPath("$.instance").value("/articles"))
                .andExpect(jsonPath("$", aMapWithSize(4)))
        ;
    }
    @Test
    @DisplayName("GET /articles/{articleId}: 500 InternalServerError で stacktrace が露出しない")
    void getArticle_500() throws Exception {
        // ## Arrange ##
        var articleId = 999L;
        when(articleService.findById(articleId)).thenThrow(RuntimeException.class);
        // ## Act ##
        var actual = mockMvc.perform(
                get("/articles/{articleId}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // ## Assert ##
        actual
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").isEmpty())
                .andExpect(jsonPath("$.instance").value("/articles/" + articleId))
                .andExpect(jsonPath("$", aMapWithSize(4)))
        ;
    }

    @Test
    @DisplayName("PUT /articles/{articleId}: 500 InternalServerError で stacktrace が露出しない")
    void putArticle_500() throws Exception {
        // ## Arrange ##
        var dummyArticleId = 0L;
        var dummyUserId = -1L;
        var loggedInUser = new LoggedInUser(dummyUserId, "dummy_username", "dummy_password", true);
        var updatedTitle = "test_title_updated";
        var updatedBody = "test_body_updated";
        when(articleService.update(dummyUserId, dummyArticleId, updatedTitle, updatedBody))
                .thenThrow(RuntimeException.class);
        var bodyJson = """
                {
                "title": "%s",
                "body": "%s"
                }
                """.formatted(updatedTitle, updatedBody);
        // ## Act ##
        var actual = mockMvc.perform(
                put("/articles/{articleId}", dummyArticleId)
                        .with(csrf())
                        .with(user(loggedInUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyJson)
        );
        // ## Assert ##
        actual
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").isEmpty())
                .andExpect(jsonPath("$.instance").value("/articles/" + dummyArticleId))
                .andExpect(jsonPath("$", aMapWithSize(4)))
        ;
    }
}
