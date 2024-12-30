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
}