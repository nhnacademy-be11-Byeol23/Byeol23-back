package com.nhnacademy.byeol23backend.bookset.book.document.service;

import com.nhnacademy.byeol23backend.bookset.book.document.BookDocument;
import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.bookcategory.service.BookCategoryService;
import com.nhnacademy.byeol23backend.bookset.bookcontributor.service.BookContributorService;
import com.nhnacademy.byeol23backend.bookset.bookimage.service.BookImageServiceImpl;
import com.nhnacademy.byeol23backend.bookset.booktag.service.BookTagService;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.contributor.domain.Contributor;
import com.nhnacademy.byeol23backend.bookset.tag.domain.Tag;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookDocumentAddBuilder implements BookDocumentBuilder {
    private final BookService bookService;
    private final BookCategoryService bookCategoryService;
    private final BookContributorService bookContributorService;
    private final BookTagService bookTagService;
    private final BookImageServiceImpl bookImageService;

    @Override
    @Transactional(readOnly = true)
    public BookDocument build(Long bookId) {
        log.info("도서 문서 추가 빌더: {}", bookId);
        Book book = bookService.getBookWithPublisher(bookId);
        List<Category> categories = bookCategoryService.getCategoriesByBookId(bookId);
        Map<String, List<Contributor>> contributorMap = bookContributorService.getContributorsByBookId(bookId).stream().collect(Collectors.groupingBy(Contributor::getContributorRole));
        List<Tag> tags = bookTagService.getTagsByBookId(bookId);

//        MQ에서 에러 뱉는거 잠시 막기 위해 수정
//        String imageUrl = bookImageService.getImageUrlsById(bookId).getFirst().getImageUrl();
        List<ImageUrlProjection> bookImages = bookImageService.getImageUrlsById(bookId);
        String imageUrl = null;

        if (!bookImages.isEmpty()) {
            // 리스트가 비어있지 않은 경우에만 첫 번째 요소 get
            imageUrl = bookImages.getFirst().getImageUrl();
        }
        log.info("image url: {}", imageUrl);

        return BookDocument.builder()
                .id(String.valueOf(book.getBookId()))
                .title(book.getBookName())
                .description(book.getDescription())
                .author(contributorMap.getOrDefault("저자", List.of()).stream().map(Contributor::getContributorName).toList())
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
                .imageUrl(imageUrl)
                .build();
    }

    @Override
    public String getEventType() {
        return "ADD";
    }
}
