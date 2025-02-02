package com.kakao.cafe.unit.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.kakao.cafe.config.QueryProps;
import com.kakao.cafe.domain.Article;
import com.kakao.cafe.repository.jdbc.ArticleJdbcRepository;
import com.kakao.cafe.repository.jdbc.KeyHolderFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleJdbcRepository 단위 테스트")
public class ArticleJdbcRepositoryTest {

    @InjectMocks
    ArticleJdbcRepository articleRepository;

    @Mock
    NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    KeyHolderFactory keyHolderFactory;

    @Mock
    QueryProps queryProps;

    Article article;

    @BeforeEach
    public void setUp() {
        article = Article.createWithoutReplyCount(1, "writer", "title", "contents",
            LocalDateTime.now());

        given(queryProps.get(any()))
            .willReturn("");
    }

    @Test
    @DisplayName("질문 객체를 저장소에 저장한다")
    public void savePersistTest() {
        // given
        article = Article.createWithInput("writer", "title", "contents");

        given(keyHolderFactory.newKeyHolder())
            .willReturn(new GeneratedKeyHolder(List.of(Map.of("article_id", 1))));

        given(jdbcTemplate.update(any(String.class), any(BeanPropertySqlParameterSource.class),
            any(KeyHolder.class)))
            .willReturn(1);

        // when
        Article savedArticle = articleRepository.save(article);

        // then
        then(savedArticle.getArticleId()).isEqualTo(1);
        then(savedArticle.getWriter()).isEqualTo("writer");
        then(savedArticle.getTitle()).isEqualTo("title");
        then(savedArticle.getContents()).isEqualTo("contents");
    }

    @Test
    @DisplayName("모든 질문 객체를 조회한다")
    public void findAllTest() {
        // given
        given(jdbcTemplate.query(any(String.class), any(RowMapper.class)))
            .willReturn(List.of(article));

        // when
        List<Article> articles = articleRepository.findAll();

        // then
        then(articles).containsExactlyElementsOf(List.of(article));
    }

    @Test
    @DisplayName("질문 id 로 질문 객체를 조회한다")
    public void findByArticleIdTest() {
        // given
        given(jdbcTemplate.queryForObject(any(String.class), any(MapSqlParameterSource.class), any(
            RowMapper.class)))
            .willReturn(article);

        // when
        Optional<Article> findArticle = articleRepository.findById(article.getArticleId());

        // then
        then(findArticle).hasValue(article);
    }

    @Test
    @DisplayName("존재하지 않는 질문 id 로 질문 객체를 조회한다")
    public void findNullTest() {
        // given
        given(jdbcTemplate.queryForObject(any(String.class), any(MapSqlParameterSource.class), any(
            RowMapper.class)))
            .willThrow(EmptyResultDataAccessException.class);

        // when
        Optional<Article> findArticle = articleRepository.findById(article.getArticleId());

        // then
        then(findArticle).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("질문 id 를 포함한 질문 객체를 저장해 업데이트한다")
    public void saveMergeTest() {
        // given
        given(jdbcTemplate.update(any(String.class), any(BeanPropertySqlParameterSource.class)))
            .willReturn(1);

        // when
        Article savedArticle = articleRepository.save(article);

        // then
        then(savedArticle.getArticleId()).isEqualTo(1);
        then(savedArticle.getWriter()).isEqualTo("writer");
        then(savedArticle.getTitle()).isEqualTo("title");
        then(savedArticle.getContents()).isEqualTo("contents");
    }

    @Test
    @DisplayName("질문 id 로 질문 객체를 삭제한다")
    public void deleteByArticleIdTest() {
        // given
        given(jdbcTemplate.update(any(String.class), any(MapSqlParameterSource.class)))
            .willReturn(1);

        // when
        articleRepository.deleteById(article.getArticleId());
    }

}

