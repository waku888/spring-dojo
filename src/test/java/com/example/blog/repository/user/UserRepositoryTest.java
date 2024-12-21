package com.example.blog.repository.user;

import com.example.blog.config.MybatisDefaultDatasourceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@MybatisDefaultDatasourceTest
class UserRepositoryTest {

    @Autowired
    private UserRepository cut;

    @Test
    void successAutowired() {
        assertThat(cut).isNotNull();
    }
    @Test
    @DisplayName("selectByUsername: 指定されたユーザー名のユーザーが存在するとき、Optional<UserEntity>を返す")
            @Sql(statements = {
            "INSERT INTO users (id, username, password, enabled) VALUES (999,'test_user1', 'test_password', true);",
            "INSERT INTO users (id, username, password, enabled) VALUES (998,'test_user2', 'test_password', true);"
            })
    void selectByUsername_success() {
        // ## Arrange ##
        // ## Act ##
        var actual = cut.selectByUsername("test_user1");
        // ## Assert ##
        assertThat(actual).hasValueSatisfying(actualEntity -> {
            assertThat(actualEntity.id()).isEqualTo(999);
            assertThat(actualEntity.username()).isEqualTo("test_user1");
            assertThat(actualEntity.password()).isEqualTo("test_password");
            assertThat(actualEntity.enabled()).isTrue();
        });
    }
}