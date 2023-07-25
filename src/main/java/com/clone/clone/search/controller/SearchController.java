package com.clone.clone.search.controller;


import com.clone.clone.article.dto.ArticleListResponseDto;
import com.clone.clone.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/keyword")
    public List<String> RecommendedKeyword(){
        return searchService.RecommendedKeyword();
    }

    @GetMapping("/search")
    public Page<ArticleListResponseDto> search(@RequestParam(name = "q") String title,
                                               @RequestParam(name = "page",defaultValue = "1") Integer page,
                                               @RequestParam(value = "size", defaultValue = "12") Integer size
                                ){
        return searchService.search(title,size,page);
    }

}
