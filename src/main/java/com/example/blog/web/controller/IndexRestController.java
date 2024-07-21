package com.example.blog.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public class IndexRestController {

    // GET /
    @GetMapping
    public ResponseEntity<Void> index() {
        return ResponseEntity
                .noContent()
                .build();
    }
}
