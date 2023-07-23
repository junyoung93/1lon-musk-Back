package com.clone.clone.article;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/main")
public class ArticleController {
    @Autowired

    private ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    //메인
    @GetMapping("/")
    public Page<Article> getArticleList(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "category", required = false) String category
    ) {
        return articleService.getArticleList(page-1, size, category);
    }

    //상세
    @GetMapping("/{id}")
    public ResponseEntity getAricle(@PathVariable Long id){
        return new ResponseEntity(articleService.getArticle(id), HttpStatus.OK);
    }
}
