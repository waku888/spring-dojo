package com.example.blog.web.controller.user;

import com.example.blog.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserRestControllerTest {
    private static final String MOCK_USERNAME = "user1";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @Test
    public void mockMvc() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("/users/me: ログイン済みユーザーがアクセスすると、200 OK でユーザー名を返す")
    @WithMockUser(username = MOCK_USERNAME)
    public void usersMe_return200() throws Exception {
        // ## Arrange ##

        // ## Act ##
        var actual = mockMvc.perform(MockMvcRequestBuilders.get("/users/me"));

        // ## Assert ##
        actual
                .andExpect(status().isOk())
                .andExpect(content().bytes(MOCK_USERNAME.getBytes()))
        ;
    }

    @Test
    @DisplayName("/users/me: 未ログインユーザーがアクセスすると、403 Forbidden を返す")
    public void usersMe_return403() throws Exception {
        // ## Arrange ##

        // ## Act ##
        var actual = mockMvc.perform(MockMvcRequestBuilders.get("/users/me"));

        // ## Assert ##
        actual.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /users：ユーザー作成に成功すると、レスポンスボディにユーザ情報/LocationにURIがセットされる")
    void createUser_success() throws Exception {
        // ## Arrange ##
        String newUserJson = """
                    {
                    "username": "username123",
                    "password": "password123"
                    }
                    """;

        // ## Act ##
        var actual =mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(newUserJson));

        // ## Assert ##
        actual
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",matchesPattern("/users/\\d+")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("username123"))
                .andExpect(jsonPath("$.password").doesNotExist())
//                .andDo(print())
        ;
    }


    @Test
    @DisplayName("DisplayName(\"POST /users：400 Bad Request > username がないとき")
    void createUser_badRequest() throws Exception {
        // ## Arrange ##
        String newUserJson = """
                    {
                    "password": "password123"
                    }
                    """;

        // ## Act ##
        var actual =mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(newUserJson));

        // ## Assert ##
        actual
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))

        ;
    }


    @Test
    @DisplayName("DisplayName(\"POST /users：すでに登録されているユーザー名を入力した場合、400 Bad Request")
    void createUser_badRequest_duplicatedUsername() throws Exception {

        // ## Arrange ##
        var duplicateUsername = "username00";
        userService.register(duplicateUsername, "test_password00");
        var newUserJson = """
                    {
                    "username": "%s",
                    "password": "password123"
                    }
                    """.formatted(duplicateUsername);

        // ## Act ##
        var actual =mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(newUserJson));


        // ## Assert ##
        actual
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors", hasItem(
                        allOf(
                                hasEntry("pointer", "#/username"),
                                hasEntry("detail", "このユーザー名はすでに使用されています")
                        )
                )))
        ;
    }

    @Test
    @DisplayName("DisplayName(POST /users：ユーザー名の規約に従っていない場合、400 Bad Request")
    void method_invalidCharacter() throws Exception {

        // ## Arrange ##
        var newUserJson = """
                    {
                    "username": ".username",
                    "password": "password123"
                    }
                    """;

        // ## Act ##
        var actual =mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(newUserJson));


        // ## Assert ##
        actual
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors", hasItem(
                        allOf(
                                hasEntry("pointer", "#/username"),
                                hasEntry("detail", "ユーザー名は3文字以上32文字以内で入力してください。半角英数字、ハイフン、アンダースコア、ドットのみを使用できます。先頭と末尾にハイフン、アンダースコア、ドットを使用することはできません。")
                        )
                )))
        ;
    }
    @Test
    @DisplayName("POST /users：password が規約に従っていない場合、400 Bad Request")
    void createUser_invalidPassword() throws Exception {

        // ## Arrange ##
        var newUserJson = """
                    {
                    "username": "username00",
                    "password": "too_short"
                    }
                    """;

        // ## Act ##
        var actual =mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(newUserJson));


        // ## Assert ##
        actual
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.instance").value("/users"))
                .andExpect(jsonPath("$.errors", hasItem(
                        allOf(
                                hasEntry("pointer", "#/password"),
                                hasEntry("detail", "パスワードは10文字以上255文字以内で入力してください。半角の英大文字、英小文字、数字、および記号のみ使用できます。")
                        )
                )))
        ;
    }

}