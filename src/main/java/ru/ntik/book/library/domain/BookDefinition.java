package ru.ntik.book.library.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.OrderBy;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import ru.ntik.book.library.domain.enums.BookState;

import java.util.*;

import static ru.ntik.book.library.util.Constants.BOOK_DEFINITION_REGION_NAME;

@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = BOOK_DEFINITION_REGION_NAME)

@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class BookDefinition extends NamedObject {
    @Embedded
    private PrintInfo printInfo;

    @Embedded
    private Rating rating;

    @Embedded
    private InstancesInfo instancesInfo;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] mainImage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_to_author",
            indexes = @Index(name = "author_book_idx", columnList = "authors_id, book_definitions_id", unique = true))
    private final Set<Author> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_to_book",
            joinColumns = @JoinColumn(name = "book_one", foreignKey =  @ForeignKey(name = "fk_book_one")),
            inverseJoinColumns = @JoinColumn(name = "book_two"), inverseForeignKey =  @ForeignKey(name = "fk_book_two"),
            indexes = @Index(name = "book_to_book_idx", columnList = "book_one, book_two", unique = true)
    )
    private final Set<BookDefinition> links = new HashSet<>();

    @OneToMany(mappedBy = "bookDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("created DESC" )
    private final Set<Review> reviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "bookDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("created DESC" )
    private final Set<BookInstance> instances = new LinkedHashSet<>();

    @OneToMany(mappedBy = "bookDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("created DESC" )
    private Set<BookOrder> bookOrders = new LinkedHashSet<>();

    public BookDefinition(String name, String description, Long creator, PrintInfo printInfo,
                          List<Author> authors, Category category) {
        super(name, description, creator);

        setPintInfo(printInfo);
        setCategory(category);
        setAuthors(authors);

        rating = new Rating();
        instancesInfo = new InstancesInfo();
    }

    public Set<Review> getReviews() {
        return Collections.unmodifiableSet(reviews);
    }

    public Set<BookInstance> getInstances() {
        return Collections.unmodifiableSet(instances);
    }

    private void setAuthors(List<Author> authors) {
        Objects.requireNonNull(authors);
        this.authors.addAll(authors);
    }

    private void setPintInfo(PrintInfo printInfo) {
        Objects.requireNonNull(printInfo);
        this.printInfo = printInfo;
    }

    public void setCategory(Category category) {
        Objects.requireNonNull(category);
        this.category = category;
    }

    public void setMainImage(byte[] image) {
        this.mainImage = image;
    }

    public void addReview(Review review) {
        Objects.requireNonNull(review);
        reviews.add(review);

        if (review.getRating() != 0) recalculateRating();
    }

    public boolean removeReview(Review review) {
        Objects.requireNonNull(review);
        boolean deleted = reviews.remove(review);
        if (deleted && review.getRating() != 0) recalculateRating();

        return deleted;
    }

    public void recalculateRating() {
        if (reviews.isEmpty()) {
            rating.resetToZero();
        } else {
            List<Review> withRating = reviews.stream().filter(r -> r.getRating() != 0).toList();
            int count = withRating.size();
            double avg = withRating.stream().mapToInt(Review::getRating).average().orElse(0);

            rating.setCommonRating(avg);
            rating.setVoteCount(count);
        }
    }

    public void addBookInstance(BookInstance bi) {
        Objects.requireNonNull(bi);
        if (instances.add(bi)) {
            instancesInfo.onAddInstance();
        }
    }

    public boolean removeBookInstance(BookInstance bi) {
        Objects.requireNonNull(bi);
        boolean removed = instances.remove(bi);
        if (removed) {
            instancesInfo.onRemoveInstance(bi.getStatus().getState() == BookState.ON_OWNER);
        }
        return removed;
    }
}
