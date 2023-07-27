package com.clone.clone.article.dto;

import com.clone.clone.article.entity.Article;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleListResponseDto {
    private Long id;
    private String title;
    private String image_url;
    private String category;
    private String date;


    public ArticleListResponseDto(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.image_url = article.getImage_url();
        this.category = article.getCategory();
        this.date = article.getDate();
    }

}
