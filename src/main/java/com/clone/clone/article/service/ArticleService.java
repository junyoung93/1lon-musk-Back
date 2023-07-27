package com.clone.clone.article.service;

import com.clone.clone.article.dto.ArticleListResponseDto;
import com.clone.clone.article.dto.ArticleResponseDto;
import com.clone.clone.article.entity.Article;
import com.clone.clone.article.repository.ArticleRepository;
import com.clone.clone.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clone.clone.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final String[] categorys = {"트위터", "테슬라", "페이팔", "스페이스X", "XAI", "도지코인", "뉴럴링크", "하이퍼루프", "솔라시티", "스타링크"};

    //메인 페이지
    public Page<ArticleListResponseDto> getArticleList(Integer page, Integer size) {
        if (page < 0) {
            throw new CustomException(OUT_OF_RANGE);
        }
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findAllByOrderByDateDesc(pageable).map(ArticleListResponseDto::new);
    }

    //상세 페이지
    public ArticleResponseDto getArticle(Long articleId) {

        return new ArticleResponseDto(articleRepository.findById(articleId).orElseThrow(() -> new CustomException(PAGE_NOT_EXIST)));
    }

    // 카테고리로 조회
    public Page<ArticleListResponseDto> getCategoryArticleList(Integer page, Integer size, String category) {
        if (page < 0) {
            throw new CustomException(OUT_OF_RANGE);
        }

        boolean categoryExists = Arrays.stream(categorys).anyMatch(category::equals);
        if (!categoryExists) {
            throw new CustomException(CATEGORY_NOT_EXIST);
        }

        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByCategoryOrderByDateDesc(category, pageable).map(ArticleListResponseDto::new);
    }


    //뉴스 목록에서  URL 크롤링
    public void getNewsDatas() throws IOException {
        log.info("뉴스 목록 url 크롤링");


        for (int i = 0; i < categorys.length; i++) {

            String News_URL = "https://www.donga.com/news/search?p=1&query=" + categorys[i] + "&check_news=91&more=1&sorting=1&search_date=1&v1=&v2=";

            Document document = Jsoup.connect(News_URL).get();

            Elements contents = document.select(".result_cont > .articleList > .rightList > .tit a");
            List<String> DetailNews = new ArrayList<>();

            for (Element content : contents) {
                String url = content.attr("abs:href");
                DetailNews.add(url);
            }
            Detail(DetailNews, categorys[i]);
        }
    }


    //뉴스 상세 크롤링
    public List<String> Detail(List<String> DetailNews, String category) throws IOException {
        log.info("뉴스 상세 크롤링");
        for (String detailArticle : DetailNews) {
            Document document = Jsoup.connect(detailArticle).get();
            String image_url = document.select(".thumb img").attr("abs:src");

            if (image_url == null || image_url.isEmpty()) {
                image_url = "default_image_url";
            }

            String title = document.select(".article_title .title").text();
            String content = document.select(".article_txt").html();
            if (!content.contains("<div class=\"view_ads01 adwrap_box\"")) {
                continue;
            }
            String htmlContent = document.select(".article_txt").html().split("<div class=\"view_ads01 adwrap_box\"")[0];
            String fullDate = document.select("span.date01").text();
            String requiredDateTime = fullDate.split("업데이트")[0].trim();
            String dateOnly;
            if (requiredDateTime.contains("입력")) {
                dateOnly = requiredDateTime.split("입력")[1].trim();
                if (dateOnly.contains(" ")) {
                    dateOnly = dateOnly.split(" ")[0];
                }
            } else {
                dateOnly = requiredDateTime;
            }


            Article article = Article.builder()
                    .image_url(image_url)
                    .title(title)
                    .content(htmlContent)
                    .date(dateOnly)
                    .category(category)
                    .build();
            articleRepository.save(article);
        }
        return null;
    }


}
