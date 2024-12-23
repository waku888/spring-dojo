package com.example.blog.web.controller.user;

import com.example.blog.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserRestController500InternalServerErrorTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    @DisplayName("MockMvc")
    void setUp_success() {
        // ## Arrange ##
        
        // ## Act ##

        // ## Assert ##
        assertThat(mockMvc).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    @DisplayName("DisplayName(\"POST /users: 500 InternalServerError のとき、スタックトレースがレスポンスに露出しない")
    void createUser_500_internalServerError() throws Exception {
        // ## Arrange ##
        var username = "username123";
        var password = "password123";
        when(userService.register(username, password)).thenThrow(new RuntimeException("サーバーエラー"));
        String newUserJson = """
                    {
                    "username": "%s",
                    "password": "%s"
                    }
                    """.formatted(username, password);

        // ## Act ##
        var actual =mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(newUserJson));

        // ## Assert ##
        actual
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").isEmpty())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.instance").isEmpty())
                .andExpect(jsonPath("$", aMapWithSize(5)))
        ;
    }
}
