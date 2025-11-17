package com.nhnacademy.byeol23backend.bookset.book.document;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
import com.nhnacademy.byeol23backend.bookset.booktag.service.BookTagService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookDocumentBuilder {
    private final BookService bookService;
    private final BookCategoryService bookCategoryService;
    private final BookContributorService bookContributorService;
    private final BookTagService bookTagService;

    @Transactional(readOnly = true)
    public BookDocument buildWithOutEmbedding(Long bookId) {
        Book book = bookService.getBookWithPublisher(bookId);
        List<Category> categories = bookCategoryService.getCategoriesByBookId(bookId);
        Map<String, List<Contributor>> contributorMap = bookContributorService.getContributorsByBookId(bookId).stream().collect(Collectors.groupingBy(Contributor::getContributorRole));
        List<Tag> tags = bookTagService.getTagsByBookId(bookId);

        return BookDocument.builder()
                .id(String.valueOf(book.getBookId()))
                .title(book.getBookName())
                .description(book.getDescription())
                .author(contributorMap.get("저자").stream().map(Contributor::getContributorName).toList())
                .translator(contributorMap.getOrDefault("역자", List.of()).stream().map(Contributor::getContributorName).toList())
                .isbn(book.getIsbn())
                .regularPrice(book.getRegularPrice().intValue())
                .salePrice(book.getSalePrice().intValue())
                .publisher(book.getPublisher().getPublisherName())
                .publishedAt(book.getPublishDate())
                .tagNames(tags.stream().map(Tag::getTagName).toList())
                .pathIds(categories.stream().map(Category::getPathId).toList())
                .pathNames(categories.stream().map(Category::getPathName).toList())
                .viewCount(book.getViewCount())
                .reviewCount(0)
                .ratingAverage(0.0f)
                .bookStatus(book.getBookStatus().name())
                .build();
    }
}
