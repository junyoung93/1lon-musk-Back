package com.clone.clone.article.repository;

import com.clone.clone.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByCategoryOrderByDateDesc(String category, Pageable pageable);
    Page<Article> findAllByOrderByDateDesc(Pageable pageable);

}
