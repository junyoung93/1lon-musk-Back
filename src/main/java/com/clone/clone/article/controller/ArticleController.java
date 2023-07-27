package com.clone.clone.article.controller;


import com.clone.clone.article.dto.ArticleListResponseDto;
import com.clone.clone.article.dto.ArticleResponseDto;
import com.clone.clone.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ArticleController {

    private final ArticleService articleService;


    //메인 페이지
    @GetMapping("/main")
    public Page<ArticleListResponseDto> getArticleList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size
    ) {
        return articleService.getArticleList(page - 1, size);
    }

    //상세 페이지
    @GetMapping("/main/{id}")
    public ArticleResponseDto getArticle(@PathVariable Long id) {
        return articleService.getArticle(id);
    }

    // 카테고리로 조회
    @GetMapping("/tag")
    public Page<ArticleListResponseDto> getCategoryArticleList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "category", required = false) String category
    ) {
        return articleService.getCategoryArticleList(page - 1, size, category);
    }

    //뉴스 크롤링
    @GetMapping("/news")
    public void getNews() throws IOException {
        articleService.getNewsDatas();
    }
}
