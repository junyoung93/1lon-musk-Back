package com.clone.clone.search.service;


import com.clone.clone.article.dto.ArticleListResponseDto;
import com.clone.clone.article.entity.Article;
import com.clone.clone.article.repository.ArticleRepository;
import com.clone.clone.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.clone.clone.exception.ErrorCode.OUT_OF_RANGE;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ArticleRepository articleRepository;

    public Page<ArticleListResponseDto> search(String title, Integer size , Integer page) {
        if(page < 1)
            throw new CustomException(OUT_OF_RANGE);
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").descending());

        String searchWord =  title.replaceAll("[^a-zA-Z0-9가-힣]", "");

        return  articleRepository.findByTitleOrContentContaining(searchWord,pageable).map(ArticleListResponseDto::new);
    }

    public List<String> RecommendedKeyword() {
        List<Article> recentArticles = articleRepository.findTop20ByOrderByIdDesc();
        Map<String, Integer> wordCounts = new HashMap<>();

        // 모든 기사 제목의 단어를 하나의 리스트로 추출하고, 단어의 등장 횟수를 카운트하여 Map에 저장
        for (Article article : recentArticles) {
            String[] words = article.getTitle().split(" ");
            for (String word : words) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        }

        // 등장 횟수가 가장 높은 상위 6개의 단어를 추출
        List<String> duplicatedWords = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String mostFrequentWord = null;
            int maxCount = 0;

            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    mostFrequentWord = entry.getKey();
                    maxCount = entry.getValue();
                }
            }

            if (mostFrequentWord != null) {
                duplicatedWords.add(mostFrequentWord);
                wordCounts.remove(mostFrequentWord);
            } else {
                // 단어가 더 이상 없으면 종료
                break;
            }
        }
        return duplicatedWords;
    }
}
