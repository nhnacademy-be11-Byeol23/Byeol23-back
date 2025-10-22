package com.nhnacademy.byeol23backend.bookset.book.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookRequestDto;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookResponseDto;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.book.service.BookService;
import com.nhnacademy.byeol23backend.bookset.publisher.domain.Publisher;
import com.nhnacademy.byeol23backend.bookset.publisher.repository.PublisherRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;

	public BookServiceImpl(BookRepository bookRepository, PublisherRepository publisherRepository) {
		this.bookRepository = bookRepository;
		this.publisherRepository = publisherRepository;
	}

	@Override
	@Transactional
	public BookResponseDto createBook(BookRequestDto requestDto) {
		log.info("도서 생성 시작: {}", requestDto.getBookName());
		Publisher publisher = publisherRepository.findById(requestDto.getPublisherId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출판사입니다: " + requestDto.getPublisherId()));

		Book book = new Book();
		book.setBookName(requestDto.getBookName());
		book.setContents(requestDto.getContents());
		book.setDescription(requestDto.getDescription());
		book.setRegularPrice(requestDto.getRegularPrice());
		book.setSalePrice(requestDto.getSalePrice());
		book.setIsbn(requestDto.getIsbn());
		book.setPublishDate(requestDto.getPublishDate());
		book.setPack(requestDto.getIsPack());
		book.setBookStatus(requestDto.getBookStatus());
		book.setStock(requestDto.getStock());
		book.setPublisher(publisher);
		book.setBookImg(requestDto.getBookImg());

		Book savedBook = bookRepository.save(book);
		log.info("도서 생성 완료: ID={}", savedBook.getBookId());
		return new BookResponseDto(savedBook);
	}

	//findByISBN
	@Override
	@Transactional(readOnly = true)
	public BookResponseDto findById(Long bookId) {
		log.info("도서 조회: ID={}", bookId);
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다: " + bookId));
		return new BookResponseDto(book);
	}

	// 부분 업데이트를 구현해야하는지 궁금합니다.
	// update 페이지에서 기존값을 default로 하여 안바꾸는 데이터도 원래걸 넣어서 전달해주는걸생각하고 전체 데이터를 넣는 식으로 구현하였습니다.
	@Override
	@Transactional
	public BookResponseDto updateBook(Long bookId, BookRequestDto requestDto) {
		log.info("도서 수정 시작: ID={}", bookId);
		Book existingBook = bookRepository.findById(bookId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다: " + bookId));

		Publisher publisher = publisherRepository.findById(requestDto.getPublisherId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출판사입니다: " + requestDto.getPublisherId()));

		existingBook.setBookName(requestDto.getBookName());
		existingBook.setContents(requestDto.getContents());
		existingBook.setDescription(requestDto.getDescription());
		existingBook.setRegularPrice(requestDto.getRegularPrice());
		existingBook.setSalePrice(requestDto.getSalePrice());
		existingBook.setIsbn(requestDto.getIsbn());
		existingBook.setPublishDate(requestDto.getPublishDate());
		existingBook.setPack(requestDto.getIsPack());
		existingBook.setBookStatus(requestDto.getBookStatus());
		existingBook.setStock(requestDto.getStock());
		existingBook.setPublisher(publisher);
		existingBook.setBookImg(requestDto.getBookImg());

		Book updatedBook = bookRepository.save(existingBook);
		log.info("도서 수정 완료: ID={}", updatedBook.getBookId());
		return new BookResponseDto(updatedBook);
	}
}