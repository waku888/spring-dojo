package com.example.blog.web.controller.article;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleRestControllerNoMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void mockMvc() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("GET /articles/{id}: 指定されたIDの記事が存在するとき、200 OK で記事データを返す")
            @Sql(statements = {
                """
                INSERT INTO articles (id, title, body, created_at,
                updated_at)
                VALUE (999, 'title_999', 'body_999', '2022-01-01 10:00:00',
                '2022-02-01 11:00:00');
                """
                    })
            public void getArticle_return200() throws Exception {
                // ## Arrange ##
                // ## Act ##
                var actual = mockMvc.perform(get("/articles/{id}", 999));
                // ## Assert ##
                    actual
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(999))
                            .andExpect(jsonPath("$.title").value("title_999"))
                            .andExpect(jsonPath("$.content").value("body_999"))
                            .andExpect(jsonPath("$.createdAt").value("2022-01-01T10:00:00"))
                            .andExpect(jsonPath("$.updatedAt").value("2022-02-01T11:00:00"))
                    ;
            }

}