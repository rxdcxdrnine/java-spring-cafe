package com.kakao.cafe.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Article {

    private Integer articleId;
    private String writer;
    private String title;
    private String contents;
    private LocalDateTime createdDate;

    // derived
    private Integer replyCount;

    public static Article createWithInput(String writer, String title, String contents) {
        return new Article(null, writer, title, contents, LocalDateTime.now(), null);
    }

    public static Article createWithoutReplyCount(Integer articleId, String writer, String title,
        String contents, LocalDateTime createdDate) {
        return new Article(articleId, writer, title, contents, createdDate, null);
    }

    public Article(Integer articleId, String writer, String title, String contents,
        LocalDateTime createdDate, Integer replyCount) {
        this.articleId = articleId;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.createdDate = createdDate;
        this.replyCount = replyCount;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getWriter() {
        return writer;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public boolean equalsId(Integer articleId) {
        if (articleId == null) {
            return false;
        }
        return Objects.equals(this.articleId, articleId);
    }

    public boolean equalsUserId(String userId) {
        return writer.equals(userId);
    }

    public Article update(String title, String contents) {
        this.title = title;
        this.contents = contents;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Article article = (Article) o;

        return Objects.equals(articleId, article.articleId);
    }

    @Override
    public String toString() {
        return "Article{" +
            "articleId=" + articleId +
            ", writer='" + writer + '\'' +
            ", title='" + title + '\'' +
            ", contents='" + contents + '\'' +
            ", createdDate=" + createdDate +
            '}';
    }

}
