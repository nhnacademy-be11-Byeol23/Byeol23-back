package com.nhnacademy.byeol23backend.image.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.nhnacademy.byeol23backend.bookset.bookimage.service.BookImageServiceImpl;
import com.nhnacademy.byeol23backend.image.ImageDomain;
import com.nhnacademy.byeol23backend.orderset.packaging.service.impl.PackagingServiceImpl;
import com.nhnacademy.byeol23backend.reviewset.reviewImage.service.ReviewImageServiceImpl;

class ImageServiceGateImplTest {

    @Test
    void book_saveAndGet() {
        BookImageServiceImpl bookService = mock(BookImageServiceImpl.class);
        PackagingServiceImpl packagingService = mock(PackagingServiceImpl.class);
        ReviewImageServiceImpl reviewService = mock(ReviewImageServiceImpl.class);

        ImageServiceGateImpl gate = new ImageServiceGateImpl(bookService, packagingService, reviewService);

        Long id = 1L;
        String url = "http://example.com/book1.jpg";

        when(bookService.saveImageUrl(id, url)).thenReturn("saved-book");
        when(bookService.getImageUrlsById(id)).thenReturn(List.of(url));

        String saved = gate.saveImageUrl(id, url, ImageDomain.BOOK);
        assertEquals("saved-book", saved);
        verify(bookService).saveImageUrl(id, url);

        List<String> urls = gate.getImageUrlsById(id, ImageDomain.BOOK);
        assertEquals(1, urls.size());
        assertEquals(url, urls.get(0));
        verify(bookService).getImageUrlsById(id);
    }

    @Test
    void packaging_saveAndGet() {
        BookImageServiceImpl bookService = mock(BookImageServiceImpl.class);
        PackagingServiceImpl packagingService = mock(PackagingServiceImpl.class);
        ReviewImageServiceImpl reviewService = mock(ReviewImageServiceImpl.class);

        ImageServiceGateImpl gate = new ImageServiceGateImpl(bookService, packagingService, reviewService);

        Long id = 2L;
        String url = "http://example.com/packaging1.jpg";

        when(packagingService.saveImageUrl(id, url)).thenReturn("saved-packaging");
        when(packagingService.getImageUrlsById(id)).thenReturn(List.of(url));

        String saved = gate.saveImageUrl(id, url, ImageDomain.PACKAGING);
        assertEquals("saved-packaging", saved);
        verify(packagingService).saveImageUrl(id, url);

        List<String> urls = gate.getImageUrlsById(id, ImageDomain.PACKAGING);
        assertEquals(1, urls.size());
        assertEquals(url, urls.get(0));
        verify(packagingService).getImageUrlsById(id);
    }

    @Test
    void review_saveAndGet() {
        BookImageServiceImpl bookService = mock(BookImageServiceImpl.class);
        PackagingServiceImpl packagingService = mock(PackagingServiceImpl.class);
        ReviewImageServiceImpl reviewService = mock(ReviewImageServiceImpl.class);

        ImageServiceGateImpl gate = new ImageServiceGateImpl(bookService, packagingService, reviewService);

        Long id = 3L;
        String url = "http://example.com/review1.jpg";

        when(reviewService.saveImageUrl(id, url)).thenReturn("saved-review");
        when(reviewService.getImageUrlsById(id)).thenReturn(List.of(url));

        String saved = gate.saveImageUrl(id, url, ImageDomain.REVIEW);
        assertEquals("saved-review", saved);
        verify(reviewService).saveImageUrl(id, url);

        List<String> urls = gate.getImageUrlsById(id, ImageDomain.REVIEW);
        assertEquals(1, urls.size());
        assertEquals(url, urls.get(0));
        verify(reviewService).getImageUrlsById(id);
    }
}