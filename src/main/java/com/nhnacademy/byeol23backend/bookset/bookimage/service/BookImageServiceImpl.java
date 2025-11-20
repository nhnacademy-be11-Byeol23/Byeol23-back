package com.nhnacademy.byeol23backend.bookset.bookimage.service;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.bookimage.domain.BookImage;
import com.nhnacademy.byeol23backend.bookset.bookimage.repository.BookImageRepository;
import com.nhnacademy.byeol23backend.bookset.outbox.BookOutbox;
import com.nhnacademy.byeol23backend.bookset.outbox.event.BookOutboxEvent;
import com.nhnacademy.byeol23backend.bookset.outbox.repository.BookOutboxRepository;
import com.nhnacademy.byeol23backend.image.domain.ImageDomain;
import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.image.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookImageServiceImpl implements ImageService {
	private final BookImageRepository bookImageRepository;
	private final BookRepository bookRepository;
    private final BookOutboxRepository bookOutboxRepository;
    private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public String saveImageUrl(Long bookId, String imageUrl) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. 도서 imageId: " + bookId));
		BookImage bookImage = new BookImage(book, imageUrl);
		BookImage img = bookImageRepository.save(bookImage);

        BookOutbox savedOutBox = bookOutboxRepository.save(new BookOutbox(bookId, BookOutbox.EventType.UPDATE_IMAGE));
        Long outboxId = savedOutBox.getId();

        log.info("[변경] 도서 아웃박스 이벤트 발행: {}", outboxId);
        eventPublisher.publishEvent(new BookOutboxEvent(outboxId));
		return img.toString();
	}

	@Override
	public List<ImageUrlProjection> getImageUrlsById(Long bookId) {
		return bookImageRepository.findUrlsAndIdsByBookId(bookId);
	}

	@Override
	@Transactional
	public String deleteImageUrlsById(Long bookId) {
		String url = (bookImageRepository.findById(bookId)
			.orElseThrow(() -> new IllegalArgumentException("해당 도서 이미지를 찾을 수 없습니다. 도서 imageId: " + bookId)))
			.getBookImageUrl();
		bookImageRepository.deleteById(bookId);
		return url;
	}

	@Override
	public boolean isSupportedDomain(ImageDomain imageDomain) {
		return imageDomain == ImageDomain.BOOK;
	}
}
