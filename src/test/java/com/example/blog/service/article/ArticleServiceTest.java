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
        var actual = cut.createArticle(expectedUser.getId(), expectedTitle, expectedBody);
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
}