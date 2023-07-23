package com.clone.clone.search;

import com.clone.clone.article.Article;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final ArticleRepository articleRepository;


    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(name = "q") String title,@RequestParam(name = "page") Integer page, HttpServletRequest request){
        log.error(String.valueOf(request.getRequestURL()));
        if(page < 1)
            return new ResponseEntity<>("페이지는 1부터 !",HttpStatus.BAD_REQUEST);

        Pageable pageable = PageRequest.of(page-1, 12,Sort.by("id").descending());
        return new ResponseEntity<>(articleRepository.findByTitle(title,pageable),HttpStatusCode.valueOf(201));
    }

    @GetMapping("/search2")
    public ResponseEntity search2( HttpServletRequest request){
        log.error(String.valueOf(request.getRequestURL()));

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
        return new ResponseEntity<>(duplicatedWords,HttpStatusCode.valueOf(201));
    }
}
