package com.clone.clone.article.responsedto;

import com.clone.clone.article.Article;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleResponseDto {
    private Long id;
    private String title;
    private String content;
    private String image_url;
    private String category;
    private LocalDateTime articleDate;



    public ArticleResponseDto(Article article){
        this.id=article.getId();
        this.title=article.getTitle();
        this.content=article.getContent();
        this.image_url=article.getImage_url();
        this.category=article.getCategory();
        this.articleDate=article.getArticleDate();
    }
}
