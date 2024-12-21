package com.example.blog.repository.user;

import com.example.blog.config.MybatisDefaultDatasourceTest;
import com.example.blog.service.user.UserEntity;
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
            assertThat(actualEntity.getId()).isEqualTo(999);
            assertThat(actualEntity.getUsername()).isEqualTo("test_user1");
            assertThat(actualEntity.getPassword()).isEqualTo("test_password");
            assertThat(actualEntity.isEnabled()).isTrue();
        });
    }

    @Test
    @DisplayName("selectByUsername: 指定されたユーザー名のユーザーが存在しないとき、Optional.emptyを返す")
    @Sql(statements = {
            "INSERT INTO users (id, username, password, enabled) VALUES (999,'test_user1', 'test_password', true);",
    })
    void selectByUsername_returnEmpty() {
        // ## Arrange ##

        // ## Act ##
        var actual = cut.selectByUsername("invalid_user\"");

        // ## Assert ##
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("selectByUsername: username にnull が指定されたとき、Optional.Empty を返す（全件検索しない）")
    @Sql(statements = {
            "INSERT INTO users (id, username, password, enabled) VALUES (999,'null', 'test_password', true);",
    })
    void selectByUsername_returnNullWhenGivenUsernameIsNull() {
        // ## Arrange ##

        // ## Act ##
        var actual = cut.selectByUsername(null);
        // ## Assert ##

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("insert: User を登録することができる。id は自動で発番される")
    void insert_success() {
        // ## Arrange ##
        var newRecord = new UserEntity(null, "test_user_1", "test_user_1_pass",true);

        // ## Act ##
        cut.insert(newRecord);

        // ## Assert ##
        assertThat(newRecord.getId())
                .describedAs("AUTO INCREMENTで設定されたidがentityのidフィールドに設定される")
                .isGreaterThanOrEqualTo(1);

        var actual = cut.selectByUsername("test_user_1");
        assertThat(actual).hasValueSatisfying(actualUserRecord -> {
            assertThat(actualUserRecord.getId()).isGreaterThanOrEqualTo(1);
            assertThat(actualUserRecord.getUsername()).isEqualTo("test_user_1");
            assertThat(actualUserRecord.getPassword()).isEqualTo("test_user_1_pass");
            assertThat(actualUserRecord.isEnabled()).isTrue();
        });
    }
}