package org.skypro.skyshop.model.search;

import java.util.UUID;

public interface Searchable {

    String searchTerm();

    String contentType();

    String objectName();

    default String getStringRepresentation() {
        return '\"' + "имя " + searchTerm() + " -объекта - тип " + contentType() + " -объекта" + '\"';
    }

    UUID getId();
}
