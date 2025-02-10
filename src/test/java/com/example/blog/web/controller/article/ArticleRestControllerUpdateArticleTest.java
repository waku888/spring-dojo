package com.example.blog.web.controller.article;

import com.example.blog.security.LoggedInUser;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleRestControllerUpdateArticleTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @MockBean
    private DateTimeService mockDateTimeService;
    @Test
    void setup() {
        // ## Arrange ##

        // ## Act ##

        // ## Assert ##
        assertThat(mockMvc).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(articleService).isNotNull();
    }

    @Test
    @DisplayName("PUT /articles/{articleId}: 記事の編集に成功する")
    void updateArticle_200() throws Exception {
        // ## Arrange ##
        when(mockDateTimeService.now())
                .thenReturn(TestDateTimeUtil.of(2020,1,1,10,20,30))
                .thenReturn(TestDateTimeUtil.of(2020,2,1,10,20,30));
        var newUser = userService.register("test_username", "test_password");
        var expectedUser = new LoggedInUser(newUser.getId(), newUser.getUsername(), newUser.getPassword(), true);
        var article = articleService.create(newUser.getId(), "test_title", "test_body");
        var updatedTitle = article.getTitle() + "_updated";
        var updatedBody = article.getBody() + "_updated";
        var bodyJson = """
                {
                 "title": "%s",
                 "body": "%s"
                }
                """.formatted(updatedTitle, updatedBody);



        // ## Act ##
        var actual = mockMvc.perform(
                put("/articles/{articleId}", article.getId())
                        .with(csrf())
                        .with(user(expectedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyJson)
        );

        // ## Assert ##
        actual
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(article.getId()))
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.body").value(updatedBody))
                .andExpect(jsonPath("$.author.id").value(expectedUser.getUserId()))
                .andExpect(jsonPath("$.author.username").value(expectedUser.getUsername()))
                .andExpect(jsonPath("$.createdAt").value(article.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt", greaterThan(article.getCreatedAt().toString())))
        ;
    }

    @Test
    @DisplayName("PUT /articles/{articleId}: 指定された記事IDが存在しないとき 404 を返す")
    void updateArticle_404() throws Exception {
        // ## Arrange ##
        var invalidArticleId = 0;
        var newUser = userService.register("test_username", "test_password");
        var expectedUser = new LoggedInUser(newUser.getId(), newUser.getUsername(), newUser.getPassword(), true);
        var bodyJson = """
                {
                "title": "test_title_update",
                "body": "test_body_updated"
                }
                """;
        // ## Act ##
        var actual = mockMvc.perform(
                put("/articles/{articleId}", invalidArticleId)
                        .with(csrf())
                        .with(user(expectedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyJson)
        );
        // ## Assert ##
        actual
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("リソースが見つかりません"))
                .andExpect(jsonPath("$.instance").value("/articles/" + invalidArticleId))
        ;
    }



//    @Test
//    @DisplayName("POST /articles: リクエストのtitle フィールドがバリデーションNGのとき、400 BadRequest")
//    void createArticles_400BadRequest() throws Exception {
//        // ## Arrange ##
//        var newUser = userService.register("test_username", "test_password");
//        var expectedUser = new LoggedInUser(newUser.getId(),
//                newUser.getUsername(), newUser.getPassword(), true);
//        var bodyJson = """
//        {
//        "title": "",
//        "body": "OK_body"
//        }
//        """;
//        // ## Act ##
//        var actual = mockMvc.perform(
//                post("/articles")
//                        .with(csrf())
//                        .with(user(expectedUser))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(bodyJson)
//        );
//        // ## Assert ##
//        actual
//                .andExpect(status().isBadRequest())
//                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("Invalid request content."))
//                                .andExpect(jsonPath("$.type").value("about:blank"))
//                                .andExpect(jsonPath("$.instance").isEmpty())
//                                .andExpect(jsonPath("$.errors", hasItem(
//                                        allOf(
//                                                hasEntry("pointer", "#/title"),
//                                                hasEntry("detail", "タイトルは1文字以上255文字以内で入力してください。")
//                                                )
//                                        )))
//        ;
//    }
//
//    @Test
//    @DisplayName("POST /articles: 未ログインユーザーは記事を作成できない")
//    void createArticle_401_noLogin() throws Exception {
//        // ## Arrange ##
//
//        // ## Act ##
//        var actual = mockMvc.perform(
//                post("/articles")
//                        .with(csrf())
////                        .with(user("user1")) // 未ログイン状態
//        );
//
//        // ## Assert ##
//        actual
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
//                .andExpect(jsonPath("$.title").value("Unauthorized"))
//                .andExpect(jsonPath("$.status").value(401))
//                .andExpect(jsonPath("$.detail").value("リクエストを実行するにはログインが必要です"))
//                .andExpect(jsonPath("$.instance").value("/articles"))
//        ;
//    }
//
//    @Test
//    @DisplayName("POST /articles: CSRFトークンが不正な場合は403 を返す")
//    void createArticle_403_invalidCsrf() throws Exception {
//        // ## Arrange ##
//
//        // ## Act ##
//        var actual = mockMvc.perform(
//                post("/articles")
////                        .with(csrf()) // CSRFトークンを付与しない
//                        .with(user("user1"))
//        );
//
//        // ## Assert ##
//        actual
//                .andExpect(status().isForbidden())
//                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
//                .andExpect(jsonPath("$.title").value("Forbidden"))
//                .andExpect(jsonPath("$.status").value(403))
//                .andExpect(jsonPath("$.detail").value("CSRFトークンが不正です"))
//                .andExpect(jsonPath("$.instance").value("/articles"))
//        ;
//    }
}