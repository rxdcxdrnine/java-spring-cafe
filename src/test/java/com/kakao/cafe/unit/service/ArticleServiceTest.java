package com.kakao.cafe.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.mockito.BDDMockito.given;

import com.kakao.cafe.domain.Article;
import com.kakao.cafe.repository.ArticleRepository;
import com.kakao.cafe.service.ArticleService;
import java.util.List;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    Article article;

    @BeforeEach
    public void setUp() {
        article = new Article("writer", "title", "contents");
    }

    @Test
    @DisplayName("글을 작성한 후 저장소에 저장한다")
    public void writeTest() {
        // given
        given(articleRepository.save(article))
            .willReturn(article);

        // when
        Article savedArticle = articleService.write(article);

        // then
        assertThat(savedArticle).isEqualTo(article);
    }

    @Test
    @DisplayName("저장소에 저장된 모든 글을 조회한다")
    public void findArticlesTest() {
        // given
        given(articleRepository.findAll())
            .willReturn(List.of(article));

        // when
        List<Article> articles = articleService.findArticles();

        // then
        assertThat(articles).containsExactlyElementsOf(List.of(article));
    }
}
