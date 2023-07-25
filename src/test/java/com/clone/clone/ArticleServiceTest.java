//package com.clone.clone;
//
//import com.clone.clone.article.responsedto.entity.Article;
//import com.clone.clone.article.responsedto.repository.ArticleRepository;
//import com.clone.clone.article.service.ArticleService;
//import com.clone.clone.article.dto.ArticleResponseDto;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.times;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ArticleServiceTest {
//
//    @Mock
//    private ArticleRepository articleRepository;
//
//    @InjectMocks
//    private ArticleService articleService;
//
//    @Test
//
//    public void getArticleTest() {
//        // 가짜 Article 객체 생성
//        Article article = new Article();
//        article.setId(1L);
//        article.setTitle("Test Title");
//        article.setContent("test content");
//        article.setImage_url("https://example.com");
//        article.setCategory("트위터");
//
//        // 가짜 Article 객체를 반환하도록 articleRepository 설정
//        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
//
//        // 테스트 실행
//        ArticleResponseDto result = articleService.getArticle(1L);
//
//        // 결과 검증
//        assertEquals(article.getTitle(), result.getTitle());
//        assertEquals(article.getContent(), result.getContent());
//        assertEquals(article.getImage_url(), result.getImage_url());
//        assertEquals(article.getCategory(), result.getCategory());
//
//
//        System.out.println("Title: " + article.getTitle());
//        System.out.println("Content: " + article.getContent());
//        System.out.println("Link: " + article.getImage_url());
//        System.out.println("Category: " + article.getCategory());
//
//        // articleRepository.findById가 한 번 호출되었는지 검증
//        verify(articleRepository, times(1)).findById(1L);
//    }
//
//}
