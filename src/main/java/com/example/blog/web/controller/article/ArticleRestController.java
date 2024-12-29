package com.example.blog.web.controller.article;

import com.example.blog.api.ArticlesApi;
import com.example.blog.model.ArticleDTO;
import com.example.blog.model.ArticleForm;
import com.example.blog.model.UserDTO;
import com.example.blog.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.OffsetDateTime;

@RestController
@RequiredArgsConstructor

public class ArticleRestController implements ArticlesApi {

    private final ArticleService articleService;

    @Override
    public ResponseEntity<ArticleDTO> createArticle(ArticleForm form) {

        var userDTO = new UserDTO();
        userDTO.setId(99L);
        userDTO.setUsername("test_user");
        var body = new ArticleDTO();
        body.setId(123L);
        body.setTitle(form.getTitle());
        body.setBody(form.getBody());
        body.setAuthor(userDTO);
        body.setCreatedAt(OffsetDateTime.now());
        body.setUpdatedAt(OffsetDateTime.now());
        return ResponseEntity
                .created(URI.create("/articles/123")) // TODO mock impl
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
