package com.clone.clone.article.dto;

import com.clone.clone.article.entity.Article;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleResponseDto {
    private Long id;
    private String title;
    private String content;
    private String image_url;
    private String category;
    private String date;



    public ArticleResponseDto(Article article){
        this.id=article.getId();
        this.title=article.getTitle();
        this.content=article.getContent();
        this.image_url=article.getImage_url();
        this.category=article.getCategory();
        this.date=article.getDate();
    }
}
