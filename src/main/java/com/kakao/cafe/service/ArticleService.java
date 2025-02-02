package com.kakao.cafe.service;

import com.kakao.cafe.domain.Article;
import com.kakao.cafe.domain.Reply;
import com.kakao.cafe.dto.ArticleResponse;
import com.kakao.cafe.dto.ArticleSaveRequest;
import com.kakao.cafe.exception.ErrorCode;
import com.kakao.cafe.exception.InvalidRequestException;
import com.kakao.cafe.exception.NotFoundException;
import com.kakao.cafe.repository.ArticleRepository;
import com.kakao.cafe.repository.ReplyRepository;
import com.kakao.cafe.session.SessionUser;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ReplyRepository replyRepository;

    public ArticleService(ArticleRepository articleRepository, ReplyRepository replyRepository) {
        this.articleRepository = articleRepository;
        this.replyRepository = replyRepository;
    }

    public ArticleResponse write(SessionUser user, ArticleSaveRequest request) {
        // request 객체에 writer 설정
        request.setWriter(user.getUserId());

        // ArticleSaveRequest DTO 객체를 Article 도메인 객체로 변환
        Article article = request.toEntity();

        // Article 도메인 객체를 저장소에 저장
        Article savedArticle = articleRepository.save(article);

        // Article 도메인 객체로부터 ArticleResponse DTO 객체로 변환
        return ArticleResponse.from(savedArticle);
    }

    public List<ArticleResponse> findArticles() {
        // List<Article> 도메인 객체를 저장소로부터 반환
        List<Article> articles = articleRepository.findAll();

        // List<Article> 도메인 객체를 List<ArticleResponse> DTO 객체로 변환
        return articles.stream()
            .map(ArticleResponse::from)
            .collect(Collectors.toList());
    }

    public ArticleResponse findArticle(Integer articleId) {
        // Article 도메인 객체를 저장소로부터 반환
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        List<Reply> replies = replyRepository.findByArticleId(articleId);

        // Article 도메인 객체와 Reply 도메인 객체 컬렉션을 ArticleResponse 도메인 객체로 변환
        return ArticleResponse.of(article, replies);
    }

    public ArticleResponse mapUserArticle(SessionUser user, Integer articleId) {
        Article article = findUserArticle(user, articleId);

        // Article 도메인 객체를 ArticleResponse DTO 로 변환
        return ArticleResponse.from(article);
    }

    public ArticleResponse updateArticle(SessionUser user, ArticleSaveRequest request,
        Integer articleId) {
        Article article = findUserArticle(user, articleId);

        // Article 도메인 객체에 대해 업데이트 요청사항을 변경
        Article updatedArticle = article.update(request.getTitle(), request.getContents());

        // 저장소에 업데이트된 Article 객체를 저장
        Article savedArticle = articleRepository.save(updatedArticle);

        // Article 도메인 객체를 ArticleResponse 객체로 변환
        return ArticleResponse.from(savedArticle);
    }


    public void deleteArticle(SessionUser user, Integer articleId) {
        findUserArticle(user, articleId);

        if (!isDeletable(user.getUserId(), articleId)) {
            throw new InvalidRequestException(ErrorCode.INVALID_ARTICLE_DELETE);
        }
        articleRepository.deleteById(articleId);
    }

    private Article findUserArticle(SessionUser user, Integer articleId) {
        // Article 도메인 객체를 저장소로부터 반환
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        // 요청한 유저가 작성한 Article 객체인지 검증
        validateUser(user, article);
        return article;
    }

    private void validateUser(SessionUser user, Article article) {
        if (!article.equalsUserId(user.getUserId())) {
            throw new InvalidRequestException(ErrorCode.INVALID_ARTICLE_WRITER);
        }
    }

    private boolean isDeletable(String userId, Integer articleId) {
        return replyRepository.countByArticleIdAndNotUserId(userId, articleId) == 0;
    }

}
