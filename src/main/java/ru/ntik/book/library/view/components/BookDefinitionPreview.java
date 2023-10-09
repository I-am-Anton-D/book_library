package ru.ntik.book.library.view.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import ru.ntik.book.library.domain.BookDefinition;

public class BookDefinitionPreview extends VerticalLayout {
    public final HorizontalLayout floatMenu = new HorizontalLayout();
    private final VerticalLayout clickableArea = new VerticalLayout();
    public BookDefinitionPreview(BookDefinition bookDefinition) {
        // UI
        getStyle().setBoxShadow("inset 0 0 0 1px var(--lumo-contrast-30pct)");
        setWidth("250px");
        setHeight("300px");

        StreamResource cover;

        // TODO: implement proper cover loading for cases, when book has one

        cover = new StreamResource("book-cover.png",
                ()->getClass().getResourceAsStream("/book-cover.png"));
        Image coverImage = new Image(cover, "[Обложка книги]");
        coverImage.setHeight("160px");
        coverImage.setWidth("160px");

        floatMenu.getStyle().set("position","relative");
        floatMenu.getStyle().set("float","right");

        clickableArea.getStyle().set("margin-top","-75px");
        clickableArea.add(
                coverImage,
                new Span(bookDefinition.getName()),
                new Span("Рейтинг: " + bookDefinition.getRating().getCommonRating() + "/5.0 звезд")
        );
        add(floatMenu,clickableArea);
    }

    /**
     * Redirecting click event subscriptions to area that is intended to be clickable
     * @param listener
     * @return
     */
    @Override
    public Registration addClickListener(ComponentEventListener<ClickEvent<VerticalLayout>> listener) {
        return clickableArea.addClickListener(listener);
    }
}
