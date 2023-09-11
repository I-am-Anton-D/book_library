package ru.ntik.book.library.view.components;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import ru.ntik.book.library.domain.BookDefinition;

public class BookDefinitionPreview extends VerticalLayout {
    public BookDefinitionPreview(BookDefinition bookDefinition) {
        // creating shadow outline
        addClassName("lumo-box-shadow-xs");
        // UI
        getStyle().setBoxShadow("inset 0 0 0 1px var(--lumo-contrast-30pct)");
        setWidth("250px");
        setHeight("300px");

        StreamResource cover;

        // TODO: implement proper cover loading for cases, when book has one

        cover = new StreamResource("book-cover.png",
                ()->getClass().getResourceAsStream("/book-cover.png"));
        Image coverImage = new Image(cover, "[Обложка книги]");
        coverImage.setHeight("200px");
        coverImage.setWidth("160px");
        add(coverImage);

        add(new Span(bookDefinition.getName()));
        add(new Span("Рейтинг: " + bookDefinition.getRating().getCommonRating() + "/5.0 звезд"));
    }
}
