package org.skypro.skyshop.model.article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.skypro.skyshop.model.search.Searchable;

import java.util.Objects;
import java.util.UUID;

public final class Article implements Searchable {
    private final String titleOfArticle;
    private final String textOfArticle;
    private final UUID id;

    public Article(String titleOfArticle, String textOfArticle) {
        this.titleOfArticle = titleOfArticle;
        this.textOfArticle = textOfArticle;
        this.id = UUID.randomUUID();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @JsonIgnore
    @Override
    public String searchTerm() {
        return titleOfArticle;
    }

    @JsonIgnore
    @Override
    public String contentType() {
        return "ARTICLE";
    }

    @Override
    public String objectName() {
        return getStringRepresentation();
    }

    @Override
    public String toString() {
        return "Название статьи: " + titleOfArticle + " - Текст статьи: " + textOfArticle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        return Objects.equals(this.titleOfArticle, ((Article) o).titleOfArticle);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(titleOfArticle);
    }
}
