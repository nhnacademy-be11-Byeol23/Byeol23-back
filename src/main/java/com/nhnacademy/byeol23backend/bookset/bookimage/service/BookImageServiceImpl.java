package com.nhnacademy.byeol23backend.bookset.bookimage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.bookset.bookimage.repository.BookImageRepository;
import com.nhnacademy.byeol23backend.image.ImageService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookImageServiceImpl implements ImageService {
	private final BookImageRepository bookImageRepository;
	private final BookRepository bookRepository;

	@Override
	public String saveImageUrl(Long bookId, String imageUrl) {
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. 도서 id: " + bookId));
		BookImage bookImage = new BookImage(book, imageUrl);
		BookImage img = bookImageRepository.save(bookImage);
		return img.toString();
	}

	@Override
	public List<String> getImageUrlsById(Long bookId) {
		Book book = bookRepository.findBookWithImagesById(bookId).orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. 도서 id: " + bookId));
		return book.getBookImageUrls().stream()
			.map(BookImage::getBookImageUrl)
			.collect(Collectors.toList());
	}
}
