package com.example.blog.web.controller.article;

import com.example.blog.model.ArticleDTO;
import com.example.blog.model.ArticleListItemDTO;
import com.example.blog.model.UserDTO;
import com.example.blog.service.article.ArticleEntity;
import org.springframework.beans.BeanUtils;

public class ArticleMapper {
    public static ArticleDTO toArticleDTO(
            ArticleEntity entity
    ) {
        var userDTO = new UserDTO();
        BeanUtils.copyProperties(entity.getAuthor(), userDTO);
        var dto = new ArticleDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setAuthor(userDTO);
        return dto;
    }
    public static ArticleListItemDTO toArticleListItemDTO(
            ArticleEntity entity
    ) {
        var userDTO = new UserDTO();
        BeanUtils.copyProperties(entity.getAuthor(), userDTO);
        var dto = new ArticleListItemDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setAuthor(userDTO);
        return dto;
    }
}
