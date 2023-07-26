package com.clone.clone.article.repository;

import com.clone.clone.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByCategoryOrderByDateDesc(String category, Pageable pageable);
    Page<Article> findAllByOrderByDateDesc(Pageable pageable);


    @Query("SELECT i FROM Article i WHERE i.title LIKE %:searchWord% OR i.content LIKE %:searchWord%")
    Page<Article> findByTitleOrContentContaining(String searchWord, Pageable pageable);

    List<Article> findTop20ByOrderByIdDesc();
}
