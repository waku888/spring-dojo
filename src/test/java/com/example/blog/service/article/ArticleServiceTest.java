package com.example.blog.service.article;

import com.example.blog.config.MybatisDefaultDatasourceTest;
import com.example.blog.repository.article.ArticleRepository;
import com.example.blog.repository.user.UserRepository;
import com.example.blog.service.DateTimeService;
import com.example.blog.service.user.UserEntity;
import com.example.blog.util.TestDateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MybatisDefaultDatasourceTest
@Import(ArticleService.class)
class ArticleServiceTest {
    @Autowired
    private ArticleService cut;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @MockBean
    private DateTimeService mockDateTimeService;

    @Test
    void setup() {
        assertThat(userRepository).isNotNull();
        assertThat(articleRepository).isNotNull();
        assertThat(cut).isNotNull();
    }
    @Test
    @DisplayName("test description")
    void method_success() {
// ## Arrange ##
        var expectedTitle = "test_title";
        var expectedBody = "test_body";
        var expectedUser = new UserEntity(null, "test_user1", "test_password1",true);
        userRepository.insert(expectedUser);

        var expectedCurrentDateTime = TestDateTimeUtil.of(2020, 1, 2, 10, 20, 30);
        when(mockDateTimeService.now()).thenReturn(expectedCurrentDateTime);

// ## Act ##
        var actual = cut.create(expectedUser.getId(), expectedTitle, expectedBody);
// ## Assert ##
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(expectedTitle);
        assertThat(actual.getBody()).isEqualTo(expectedBody);
        assertThat(actual.getAuthor()).satisfies(user -> {
            assertThat(user.getId()).isEqualTo(expectedUser.getId());
            assertThat(user.getUsername()).isEqualTo(expectedUser.getUsername());
            assertThat(user.getPassword()).isNull();
            assertThat(user.isEnabled()).isEqualTo(expectedUser.isEnabled());
        });
        assertThat(actual.getCreatedAt()).isEqualTo(expectedCurrentDateTime);
        assertThat(actual.getUpdatedAt()).isEqualTo(expectedCurrentDateTime);
    }


    @Test
    @DisplayName("findAll: 記事が存在しないとき、空のリストが取得できる")
    @Sql(statements = {
            "DELETE FROM articles;"
    })
    void findAll_returnEmptyList() {
        // ## Act ##
        var actual = cut.findAll();
        // ## Assert ##
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("findAll: 記事が存在するとき、リストを返す")
    @Sql(statements = {
            "DELETE FROM articles;"
    })
    void findAll_returnMultipleArticle() {
        // ## Arrange ##
        when(mockDateTimeService.now())
                .thenReturn(TestDateTimeUtil.of(2021, 1, 1, 10, 20, 30))
                .thenReturn(TestDateTimeUtil.of(2022, 2, 2, 10, 20, 30));
        var user1 = new UserEntity();
        user1.setUsername("test_username1");
        user1.setPassword("test_password1");
        user1.setEnabled(true);
        userRepository.insert(user1);
        var expectedArticle1 = cut.create(user1.getId(), "test_title1",
                "test_body1");
        var expectedArticle2 = cut.create(user1.getId(), "test_title2",
                "test_body2");
        // ## Act ##
        var actual = cut.findAll();
        // ## Assert ##
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isEqualTo(expectedArticle2);
        assertThat(actual.get(1)).isEqualTo(expectedArticle1);
    }
}