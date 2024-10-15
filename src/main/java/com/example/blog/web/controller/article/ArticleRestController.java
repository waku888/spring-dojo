package com.example.blog.web.controller.article;

import com.example.blog.service.article.ArticleService;
import com.example.blog.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class ArticleRestController {
    private final ArticleService articleService;

//    public ArticleRestController(ArticleService articleService){
//        this.articleService = articleService;
//    }
    // GET /articles/1
    @GetMapping("/articles/{id}")
    public ArticleDTO showArticle(@PathVariable("id") long id){
        return articleService.findById(id) // Optional<ArticleEntity>
                .map(ArticleDTO::from)//Optional<ArticleDTO>
                .orElseThrow(ResourceNotFoundException::new);
    }
}
