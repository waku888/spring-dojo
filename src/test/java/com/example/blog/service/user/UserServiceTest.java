package com.example.blog.service.user;

import com.example.blog.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService cut;
    @Autowired
    private UserRepository userRepository;

    @Test
    void successAutowired() {
        assertThat(cut).isNotNull();
        assertThat(userRepository).isNotNull();
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
}