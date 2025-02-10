package com.example.blog.web.controller.article;

import com.example.blog.service.DateTimeService;
import com.example.blog.service.article.ArticleService;
import com.example.blog.service.user.UserService;
import com.example.blog.util.TestDateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleRestControllerListArticlesTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @MockBean
    private DateTimeService mockDateTimeService;
    @Test
    void setup() {
        // ## Arrange ##

        // ## Act ##

        // ## Assert ##
        assertThat(mockMvc).isNotNull();
        assertThat(articleService).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    @DisplayName("GET /articles: 記事の一覧を取得できる")
    @Sql(statements =  {"""
            DELETE FROM articles;
            """
    })
    void listArticles_success() throws Exception {
        // ## Arrange ##
        when(mockDateTimeService.now())
                .thenReturn(TestDateTimeUtil.of(2020,1,1,10,20,30))
                .thenReturn(TestDateTimeUtil.of(2020,2,1,10,20,30));
        var expectedUser1 =  userService.register("test_username1", "test_password1");
        var expectedUser2 =  userService.register("test_username2", "test_password2");
        var expectedArticle1 = articleService.create(expectedUser1.getId(),"test_title1", "test_body1");
        var expectedArticle2 = articleService.create(expectedUser2.getId(),"test_title2", "test_body2");

        // ## Act ##
         var actual = mockMvc.perform(
                 get("/articles")
                       .contentType(MediaType.APPLICATION_JSON)
        );
        // ## Assert ##
        // response header
        actual
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        ;
        // response body: item[0]
        actual
                .andExpect(jsonPath("$.items[0].id").value(expectedArticle2.getId()))
                .andExpect(jsonPath("$.items[0].title").value(expectedArticle2.getTitle()))
                .andExpect(jsonPath("$.items[0].createdAt").value(expectedArticle2.getCreatedAt().toString()))
                .andExpect(jsonPath("$.items[0].updatedAt").value(expectedArticle2.getUpdatedAt().toString()))
                .andExpect(jsonPath("$.items[0].author.id").value(expectedUser2.getId()))
                .andExpect(jsonPath("$.items[0].author.username").value(expectedArticle2.getAuthor().getUsername()))
        ;
        // response body: item[1]
        actual
                .andExpect(jsonPath("$.items[1].id").value(expectedArticle1.getId()))
                .andExpect(jsonPath("$.items[1].title").value(expectedArticle1.getTitle()))
                .andExpect(jsonPath("$.items[1].createdAt").value(expectedArticle1.getCreatedAt().toString()))
                .andExpect(jsonPath("$.items[1].updatedAt").value(expectedArticle1.getUpdatedAt().toString()))
                .andExpect(jsonPath("$.items[1].author.id").value(expectedUser1.getId()))
                .andExpect(jsonPath("$.items[1].author.username").value(expectedArticle1.getAuthor().getUsername()))
        ;
    }
}