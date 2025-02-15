package com.example.blog.repository.article;

import com.example.blog.config.MybatisDefaultDatasourceTest;
import com.example.blog.repository.user.UserRepository;
import com.example.blog.service.article.ArticleEntity;
import com.example.blog.service.user.UserEntity;
import com.example.blog.util.TestDateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;


//@MybatisTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //InMemorydbを使わずにMySqlを使うようにする
@MybatisDefaultDatasourceTest
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository cut;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() {
        assertThat(cut).isNotNull();
    }

    @Test
    @DisplayName("selectById: 引数で指定されたIDの記事が存在するとき、ArticleEntity を返す")
    @Sql(statements = {"""
            DELETE FROM articles;
            DELETE FROM users;

            INSERT INTO users (id, username, password, enabled)
            VALUES (1, 'test_user1', 'test_password_1', true);
            
            INSERT INTO articles (id, user_id, title, body, created_at, updated_at)
            VALUES (999, 1, 'title_999', 'content_999', '2020-10-01 00:00:00', '2020-11-01 00:00:00');
            """
    })
    public void selectById_returnArticleEntity() {
        // ## Arrange ##

        // ## Act ##
        var actual = cut.selectById(999);
        // ## Assert ##
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(article -> {
                    assertThat(article.getId()).isEqualTo(999);
                    assertThat(article.getTitle()).isEqualTo("title_999");
                    assertThat(article.getBody()).isEqualTo("content_999");
                    assertThat(article.getCreatedAt()).isEqualTo("2020-10-01T00:00:00+09:00");
                    assertThat(article.getUpdatedAt()).isEqualTo("2020-11-01T00:00:00+09:00");

                    assertThat(article.getAuthor().getId()).isEqualTo(1);
                    assertThat(article.getAuthor().getUsername()).isEqualTo("test_user1");
                    assertThat(article.getAuthor().getPassword()).isNull();
                    assertThat(article.getAuthor().isEnabled()).isTrue();

                });
    }
    @Test
    @DisplayName("selectById: 引数で指定されたIDの記事が存在しないとき、空のOptionalを返す")
    public void selectById_returnEmpty() {
        // ## Arrange ##
        // ## Act ##
        var actual = cut.selectById(-9);
        // ## Assert ##
        assertThat(actual).isEmpty();
    }
    @Test
    @DisplayName("insert：記事データの作成に成功する")
    void insert_success() {
        // ## Arrange ##
        var expectedUser = new UserEntity(null, "test_username", "test_password", true);
        userRepository.insert(expectedUser);
        var expectedEntity = new ArticleEntity(
                null,
                "test_title",
                "test_body",
                expectedUser,
                TestDateTimeUtil.of(2020, 1, 1, 10, 30, 40),
                TestDateTimeUtil.of(2021, 1, 1, 10, 30, 40)
        );

        // ## Act ##
        cut.insert(expectedEntity);

        // ## Assert ##
        var actualOpt = cut.selectById(expectedEntity.getId());
        assertThat(actualOpt).hasValueSatisfying(actualEntity -> {
            assertThat(actualEntity.getId()).isEqualTo(expectedEntity.getId());
            assertThat(actualEntity.getTitle()).isEqualTo(expectedEntity.getTitle());
            assertThat(actualEntity.getBody()).isEqualTo(expectedEntity.getBody());
            assertThat(actualEntity.getCreatedAt()).isEqualTo(expectedEntity.getCreatedAt());
            assertThat(actualEntity.getUpdatedAt()).isEqualTo(expectedEntity.getUpdatedAt()
            );
        });
    }

    @Test
    @DisplayName("selectAll：記事が存在しないとき、空のリストを返す")
    @Sql(statements = {
            "DELETE FROM articles;"
    })
    void selectAll_returnEmptyList() {
        // ## Act ##
        var actual = cut.selectAll();
        // ## Assert ##
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("selectAll：複数件のArticleEntity を返す")
    @Sql(statements = {
            "DELETE FROM articles;"
    })
    void selectAll_returnMultipleArticles() {
        // ## Arrange ##
        var expectedUser = new UserEntity(null, "test_username",
                "test_password", true);
        userRepository.insert(expectedUser);
        var datetime1 = TestDateTimeUtil.of(2022, 1, 1, 10, 20, 30);
        var datetime2 = TestDateTimeUtil.of(2022, 2, 1, 10, 20, 30);
        var expectedArticle1 = new ArticleEntity(null, "title1", "body1",
                expectedUser, datetime1, datetime1);
        var expectedArticle2 = new ArticleEntity(null, "title1", "body1",
                expectedUser, datetime2, datetime2);
        cut.insert(expectedArticle1);
        cut.insert(expectedArticle2);
        // ## Act ##
        var actual = cut.selectAll();
        // ## Assert ##
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0))
                .usingRecursiveComparison()
                .ignoringFields("author.password")
                .isEqualTo(expectedArticle2);
        assertThat(actual.get(1))
                .usingRecursiveComparison()
                .ignoringFields("author.password")
                .isEqualTo(expectedArticle1);
    }

    @Test
    @DisplayName("update: 記事の title/body/updatedAt を更新する")
    void update_success() {
        // ## Arrange ##
        var expectedTitle = "updated_title";
        var expectedBody = "updated_body";
        var expectedCreatedAt = TestDateTimeUtil.of(2020, 1, 1, 10, 30, 40);
        var expectedUpdatedAt = expectedCreatedAt.plusDays(1);
        var author = new UserEntity();
        author.setUsername("test_username");
        author.setPassword("test_password");
        author.setEnabled(true);
        userRepository.insert(author);
        var articleToCreate = new ArticleEntity();
        articleToCreate.setTitle("test_title");
        articleToCreate.setTitle("test_body");
        articleToCreate.setAuthor(author);
        articleToCreate.setCreatedAt(expectedCreatedAt);
        articleToCreate.setUpdatedAt(expectedCreatedAt);
        cut.insert(articleToCreate);
        var articleToUpdate = new ArticleEntity(
                articleToCreate.getId(),
                expectedTitle,
                expectedBody,
                author,
                articleToCreate.getCreatedAt(),
                expectedUpdatedAt
        );
        // ## Act ##
        cut.update(articleToUpdate);
        // ## Assert ##
        var actual = cut.selectById(articleToCreate.getId());
        assertThat(actual).hasValueSatisfying(actualArticle -> {
            assertThat(actualArticle)
                    .usingRecursiveComparison()
                    .ignoringFields("author.password")
                    .isEqualTo(articleToUpdate);
        });
    }
}