package com.example.blog.web.controller.user;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserRestControllerTest {
    private static final String MOCK_USERNAME = "user1";

    @Autowired
    private MockMvc mockMvc;

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
        actual.andExpect(status().isForbidden());
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
        actual.andExpect(status().isCreated());
    }
    
}