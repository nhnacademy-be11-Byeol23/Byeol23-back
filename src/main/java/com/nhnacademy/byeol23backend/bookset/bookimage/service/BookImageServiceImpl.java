package com.nhnacademy.byeol23backend.bookset.bookimage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.bookset.bookimage.repository.BookImageRepository;
import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.image.service.ImageService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookImageServiceImpl implements ImageService {
	private final BookImageRepository bookImageRepository;
	private final BookRepository bookRepository;

	@Override
	public String saveImageUrl(Long bookId, String imageUrl) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. 도서 imageId: " + bookId));
		BookImage bookImage = new BookImage(book, imageUrl);
		BookImage img = bookImageRepository.save(bookImage);
		return img.toString();
	}

	@Override
	public List<ImageUrlProjection> getImageUrlsById(Long bookId) {
		return bookImageRepository.findUrlsAndIdsByBookId(bookId);
	}

	@Override
	public String deleteImageUrlsById(Long bookId) {
		String url = (bookImageRepository.findById(bookId)
			.orElseThrow(() -> new IllegalArgumentException("해당 도서 이미지를 찾을 수 없습니다. 도서 imageId: " + bookId)))
			.getBookImageUrl();
		bookImageRepository.deleteById(bookId);
		return url;
	}

	@Override
	public ImageDomain getImageDomain() {
		return ImageDomain.BOOK;
	}
}
