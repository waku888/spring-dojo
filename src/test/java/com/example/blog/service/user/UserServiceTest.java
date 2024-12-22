package com.example.blog.service.user;

import com.example.blog.config.MybatisDefaultDatasourceTest;
import com.example.blog.config.PasswordEncoderConfig;
import com.example.blog.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@MybatisDefaultDatasourceTest
@Import({UserService.class, PasswordEncoderConfig.class})
class UserServiceTest {

    @Autowired
    private UserService cut;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationContext ctx;

    @Test
    void successAutowired() {
        assertThat(cut).isNotNull();
        assertThat(userRepository).isNotNull();
        System.out.println("bean.length = " + ctx.getBeanDefinitionNames().length);
    }

    @Test
    @DisplayName("register: パスワードがハッシュ化されてデータベースに登録される")
    void register_success() {
        // ## Arrange ##
        var Username = "test_username";
        var password = "test_password";
        // ## Act ##
        cut.register(Username, password);

        // ## Assert ##
        var actual = userRepository.selectByUsername(Username);
        assertThat(actual).hasValueSatisfying(actualUserEntity -> {
            assertThat(actualUserEntity.getPassword())
                    .describedAs("パスワードはハッシュ化してデータベースに保存する")
                    .isNotEmpty()
                    .isNotEqualTo(password);
            assertThat(actualUserEntity.isEnabled())
                    .describedAs("新規登録ユーザーはenabled 状態で登録される")
                    .isTrue();
        });
    }

    @Test
    @DisplayName("existsUsername: ユーザー名がすでに存在するときtrue")
    void existsUsername_returnTrue() {
        // ## Arrange ##
        var username = "test_username";
        var alreadyExistUser = new UserEntity(null, username, "test_password", true);
        userRepository.insert(alreadyExistUser);

        // ## Act ##
        var actual = cut.existsUsername(username);

        // ## Assert ##
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("existsUsername: ユーザー名が未登録のとき false")
    void existsUsername_returnFalse() {
        // ## Arrange ##
        var username = "test_username";
        var alreadyExistUser = new UserEntity(null, "dummy_username", "test_password", true);
        userRepository.insert(alreadyExistUser);

        // ## Act ##
        var actual = cut.existsUsername("new_username");

        // ## Assert ##
        assertThat(actual).isFalse();
    }

}