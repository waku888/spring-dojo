package com.example.blog.web.controller.article;

import com.example.blog.config.ObjectMapperConfig;
import com.example.blog.config.PasswordEncoderConfig;
import com.example.blog.config.SecurityConfig;
import com.example.blog.service.article.ArticleEntity;
import com.example.blog.service.article.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleRestController.class)
@Import({ObjectMapperConfig.class, SecurityConfig.class, PasswordEncoderConfig.class})
class ArticleRestControllerMockTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService mockArticleService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void mockMvc() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("GET /articles/{id}: 指定されたIDの記事が存在するとき、200 OK")
    public void getArticlesById_200OK() throws Exception {
        // ## Arrange ##
        var expected = new ArticleEntity(
                999L,
                "title_999",
                "content_999",
                null, LocalDateTime.of(2022, 1, 2, 3, 4, 5),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5)
        );
        when(mockArticleService.findById(expected.getId())).thenReturn(Optional.of(expected));
        // ## Act ##
        var actual = mockMvc.perform(get("/articles/{id}",expected.getId()));
        // ## Assert ##
        actual
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.title").value(expected.getTitle()))
                .andExpect(jsonPath("$.content").value(expected.getContent()))
                .andExpect(jsonPath("$.createdAt").value(expected.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(expected.getUpdatedAt().toString()))
        ;
    }

    @Test
    @DisplayName("GET /articles/{id}: 指定されたIDの記事が存在しないとき、404NotFound")
            public void getArticlesById_404NotFound() throws Exception {
            // ## Arrange ##
            var expectedId = 999;
            when(mockArticleService.findById(expectedId)).thenReturn(Optional.empty());
            // ## Act ##
            var actual = mockMvc.perform(get("/articles/{id}", expectedId));
            // ## Assert ##
            actual.andExpect(status().isNotFound());
            }

}