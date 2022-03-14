package com.kakao.cafe.service;

import com.kakao.cafe.domain.Article;
import com.kakao.cafe.dto.ArticleResponse;
import com.kakao.cafe.dto.ArticleSaveRequest;
import com.kakao.cafe.dto.UserResponse;
import com.kakao.cafe.exception.ErrorCode;
import com.kakao.cafe.exception.NotFoundException;
import com.kakao.cafe.repository.ArticleRepository;
import com.kakao.cafe.repository.UserRepository;
import com.kakao.cafe.util.Mapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public ArticleResponse write(UserResponse userResponse, ArticleSaveRequest request) {
        // request 객체에 writer 설정
        request.setWriter(userResponse.getUserId());

        // ArticleSaveRequest DTO 객체를 Article 도메인 객체로 변환
        Article article = Mapper.map(request, Article.class);

        // 회원가입하지 않은 유저 이름으로 글을 작성 시 예외 처리
        userRepository.findByUserId(article.getWriter())
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Article 도메인 객체를 저장소에 저장
        Article savedArticle = articleRepository.save(article);

        // Article 도메인 객체로부터 ArticleResponse DTO 객체로 변환
        return Mapper.map(savedArticle, ArticleResponse.class);
    }

    public List<ArticleResponse> findArticles() {
        // List<Article> 도메인 객체를 저장소로부터 반환
        List<Article> articles = articleRepository.findAll();

        // List<Article> 도메인 객체를 List<ArticleResponse> DTO 객체로 변환
        return articles.stream()
            .map(article -> Mapper.map(article, ArticleResponse.class))
            .collect(Collectors.toList());
    }

    public ArticleResponse findArticle(Integer articleId) {
        // Article 도메인 객체를 저장소로부터 반환
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        // Article 도메인 객체를 ArticleResponse 도메인 객체로 변환
        return Mapper.map(article, ArticleResponse.class);
    }
}
