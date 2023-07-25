package com.clone.clone.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByCategoryOrderByArticleDateDesc(String category, Pageable pageable);
    Page<Article> findAllByOrderByArticleDateDesc(Pageable pageable);

}
