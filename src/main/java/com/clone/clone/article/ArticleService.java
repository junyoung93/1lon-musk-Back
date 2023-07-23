package com.clone.clone.article;

import com.clone.clone.article.responsedto.ArticleResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    //메인 페이지
    public Page<Article> getArticleList(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size);
        if (category != null) {
            return articleRepository.findByCategoryOrderByArticleDateDesc(category, pageable);
        } else {
            return articleRepository.findAllByOrderByArticleDateDesc(pageable);
        }

    }

    //상세 페이지
    public ArticleResponseDto getArticle(Long articleId) {
        return new ArticleResponseDto(articleRepository.findById(articleId).orElseThrow(() -> new IllegalArgumentException("에러")));
    }



}
