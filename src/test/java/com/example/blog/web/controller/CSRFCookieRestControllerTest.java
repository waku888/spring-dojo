package com.example.blog.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CSRFCookieRestControllerTest {

    @Nested
    class Return204Test {
        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("/csrf-cookie: GET リクエストを送ると、204 No Content が返る")
        public void getCsrfCookie_return204() throws Exception {
            mockMvc.perform(get("/csrf-cookie"))
                    .andExpect(status().isNoContent())
                    .andExpect(header().string("Set-Cookie",
                            containsString("XSRF-TOKEN=")));
        }

    }

    @Nested
    class Return500Test {
        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CSRFCookieRestController mockCsrfCookieRestController;

        @Test
        @DisplayName("/csrf-cookie: GET リクエストの処理でException が発生すると、\n" +
                "500 Internal Server Error が返る")
        public void getCsrfCookie_return204() throws Exception {
            // ## Arrange ##
            doThrow(new RuntimeException("error")).when(mockCsrfCookieRestController).getCsrfCookie();

            // ## Act ##
            var result = mockMvc.perform(get("/csrf-cookie"));

            // ## Assert ##
            result
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Internal Server Error"))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.detail").isEmpty())
                    .andExpect(jsonPath("$.instance").value("/csrf-cookie"))
                    .andExpect(jsonPath("$", aMapWithSize(4)))
            ;
        }

    }
}