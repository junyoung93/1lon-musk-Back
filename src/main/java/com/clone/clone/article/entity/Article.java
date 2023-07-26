package com.clone.clone.article.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Table
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 5000)
    private String title;
    @Column(nullable = false,length = 10000)
    private String content;
    @Column(nullable = false)
    private String image_url;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String date;

}
