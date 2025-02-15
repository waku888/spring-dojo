package com.example.blog.service.article;

import com.example.blog.config.MybatisDefaultDatasourceTest;
import com.example.blog.config.PasswordEncoderConfig;
import com.example.blog.repository.article.ArticleRepository;
import com.example.blog.repository.user.UserRepository;
import com.example.blog.service.DateTimeService;
import com.example.blog.service.exceotion.ResourceNotFoundException;
import com.example.blog.service.exceotion.UnauthorizedResourceAccessException;
import com.example.blog.service.user.UserEntity;
import com.example.blog.service.user.UserService;
import com.example.blog.util.TestDateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@MybatisDefaultDatasourceTest
@Import({
        ArticleService.class,
        UserService.class,
        PasswordEncoderConfig.class
})
class ArticleServiceTest {
    @Autowired
    private ArticleService cut;
    @Autowired
    private UserService userService;
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

    @Test
    @DisplayName("update: 記事を更新できる")
    void update_success() {
        // ## Arrange ##
        var expectedUpdatedAt = TestDateTimeUtil.of(2020, 1, 10, 10, 10, 10);
        when(mockDateTimeService.now())
                .thenReturn(expectedUpdatedAt.minusDays(1))
                .thenReturn(expectedUpdatedAt);
        var user = userService.register("test_username", "test_password");
        var article = cut.create(user.getId(), "test_title", "test_body");
        var expectedTitle = "updated_title";
        var expectedBody = "updated_body";
        // ## Act ##
        var actual = cut.update(user.getId(), article.getId(), expectedTitle, expectedBody);
        // ## Assert ##
        // assert return value of ArticleService#update
        assertThat(actual.getId()).isEqualTo(article.getId());
        assertThat(actual.getTitle()).isEqualTo(expectedTitle);
        assertThat(actual.getBody()).isEqualTo(expectedBody);
        assertThat(actual.getCreatedAt()).isEqualTo(article.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
        assertThat(actual.getAuthor().getId()).isEqualTo(user.getId());
        assertThat(actual.getAuthor().getUsername()).isEqualTo(user.getUsername());
        assertThat(actual.getAuthor().getPassword()).isNull();
        assertThat(actual.getAuthor().isEnabled()).isEqualTo(user.isEnabled());
        // assert record in articles table
        var actualRecordOpt = articleRepository.selectById(article.getId());
        assertThat(actualRecordOpt).hasValueSatisfying(actualRecord -> {
            assertThat(actualRecord.getId()).isEqualTo(article.getId());
            assertThat(actualRecord.getTitle()).isEqualTo(expectedTitle);
            assertThat(actualRecord.getBody()).isEqualTo(expectedBody);
            assertThat(actualRecord.getCreatedAt()).isEqualTo(article.getCreatedAt());
            assertThat(actualRecord.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
            assertThat(actualRecord.getAuthor().getId()).isEqualTo(user.getId());
            assertThat(actualRecord.getAuthor().getUsername()).isEqualTo(user.getUsername());
            assertThat(actualRecord.getAuthor().getPassword()).isNull();
            assertThat(actualRecord.getAuthor().isEnabled()).isEqualTo(user.isEnabled());
        });
    }
    @Test
    @DisplayName("update: 指定された記事が見つからないとき ResourceNotFoundException を throw する")
    void update_throwResourceNotFoundException() {
        // ## Arrange ##
        var user = new UserEntity();
        user.setUsername("test_user1");
        user.setPassword("test_password1");
        user.setEnabled(true);
        userRepository.insert(user);
        var invalidArticleId = 0L;
//        var user = userService.register("test_username", "test_password");
        // ## Act & Assert ##
        assertThrows(ResourceNotFoundException.class, () -> {
            cut.update(user.getId(), invalidArticleId, "updated_title", "updated_body");
        });

    }

    @Test
    @DisplayName("update: 自分以外が作成した記事を編集しようとしたとき UnauthorizedResourceAccessException を throw する")
    void update_throwUnauthorizedResourceAccessException() {
        // ## Arrange ##
        var expectedUpdatedAt = TestDateTimeUtil.of(2020, 1, 10, 10, 10, 10);
        when(mockDateTimeService.now()).thenReturn(expectedUpdatedAt);
        var author = userService.register("test_username", "test_password");
        var article = cut.create(author.getId(), "test_title", "test_body");
        var otherUser = userService.register("other_username", "other_password");
        // ## Act & Assert ##
        assertThrows(UnauthorizedResourceAccessException.class, () -> {
            cut.update(
                    otherUser.getId(),
                    article.getId(),
                    "updated_title",
                    "updated_body"
            );
        });
    }
    @Test
    @DisplayName("delete: 記事の削除に成功する")
    void delete_success() {
        // ## Arrange ##
        var expectedUpdatedAt = TestDateTimeUtil.of(2020, 1, 10, 10, 10, 10);
        when(mockDateTimeService.now())
                .thenReturn(expectedUpdatedAt.minusDays(1))
                .thenReturn(expectedUpdatedAt);
        var expectedUser = new UserEntity();
        expectedUser.setUsername("test_user1");
        expectedUser.setPassword("test_password1");
        expectedUser.setEnabled(true);
        userRepository.insert(expectedUser);
        var existingArticle = cut.create(expectedUser.getId(), "test_title", "test_body");
        // ## Act ##
        cut.delete(expectedUser.getId(), existingArticle.getId());
        // ## Assert ##
        var actual = articleRepository.selectById(existingArticle.getId());
        assertThat(actual).isEmpty();
    }
    @Test
    @DisplayName("delete: 指定された ID の記事が見つからないとき、ResourceNotFoundException を throw する")
    void delete_throwResourceNotFoundException() {
        // ## Arrange ##
        var expectedUser = new UserEntity();
        expectedUser.setUsername("test_user1");
        expectedUser.setPassword("test_password1");
        expectedUser.setEnabled(true);
        userRepository.insert(expectedUser);
        var invalidArticleId = 0L;
        // ## Act & Assert ##
        assertThrows(ResourceNotFoundException.class, () -> {
            cut.delete(expectedUser.getId(), invalidArticleId);
        });
    }
}

