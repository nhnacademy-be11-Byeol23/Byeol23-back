package com.nhnacademy.byeol23backend.cartset.cart.service.impl;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.cartset.cart.dto.CustomerIdentifier;
import com.nhnacademy.byeol23backend.cartset.cart.service.CartService;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookAddRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookResponse;
import com.nhnacademy.byeol23backend.cartset.cartbook.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23backend.cartset.cartbook.service.CartBookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestCartService implements CartService {
	private final StringRedisTemplate stringRedisTemplate;
	private final CartBookService cartBookService;
	private final BookRepository bookRepository;

	@Override
	public void addBook(CustomerIdentifier identifier, CartBookAddRequest request) {
		if (!bookRepository.existsById(request.bookId())) {
			throw new BookNotFoundException("찾을 수 없는 도서 입니다.");
		}

		String cartHashKey = getCartHashKey(identifier.guestId());
		String cartSortedSetKey = getCartSortedSetKey(identifier.guestId());
		stringRedisTemplate.execute(new SessionCallback<>() {
			@Override
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				RedisOperations<String, String> ops = operations;
				ops.multi();

				ops.opsForHash().increment(cartHashKey, String.valueOf(request.bookId()), request.quantity());
				ops.opsForZSet().add(cartSortedSetKey, String.valueOf(request.bookId()), generateScore());

				List<Object> result = ops.exec();
				if (!result.isEmpty()) {
					log.info("장바구니 {}번 도서 {}개 추가", request.bookId(), request.quantity());
				} else {
					log.info("장바구니 {}번 도서 추가 실패", request.bookId());
				}
				return result;
			}
		});
		stringRedisTemplate.expire(cartHashKey, Duration.ofHours(1));
		stringRedisTemplate.expire(cartSortedSetKey, Duration.ofHours(1));
	}

	@Override
	public List<CartBookResponse> getCartBooks(CustomerIdentifier identifier) {
		Set<String> strBookIds = stringRedisTemplate.opsForZSet()
			.reverseRange(getCartSortedSetKey(identifier.guestId()), 0, -1);
		if (strBookIds == null || strBookIds.isEmpty())
			return List.of();
		List<Long> bookIds = strBookIds.stream().map(Long::valueOf).toList();

		Map<Object, Object> cartBookMap = stringRedisTemplate.opsForHash()
			.entries(getCartHashKey(identifier.guestId()));
		return cartBookService.getCartBooks(bookIds, cartBookMap);
	}

	@Override
	public void updateQuantity(CustomerIdentifier identifier, Long bookId, CartBookUpdateRequest request) {
		String cartHashKey = getCartHashKey(identifier.guestId());
		String cartSortedSetKey = getCartSortedSetKey(identifier.guestId());
		stringRedisTemplate.opsForHash().put(cartHashKey, String.valueOf(bookId), String.valueOf(request.quantity()));
		stringRedisTemplate.expire(cartHashKey, Duration.ofHours(1));
		stringRedisTemplate.expire(cartSortedSetKey, Duration.ofHours(1));

		log.info("장바구니 {}번 도서 {}개로 변경", bookId, request.quantity());
	}

	@Override
	public void deleteBook(CustomerIdentifier identifier, Long bookId) {
		String cartHashKey = getCartHashKey(identifier.guestId());
		String cartSortedSetKey = getCartSortedSetKey(identifier.guestId());
		stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				RedisOperations<String, String> ops = operations;
				ops.multi();

				ops.opsForHash().delete(cartHashKey, String.valueOf(bookId));
				ops.opsForZSet().remove(cartSortedSetKey, String.valueOf(bookId));

				List<Object> result = ops.exec();
				log.info("장바구니에 {}번 도서 삭제", bookId);
				return result;
			}
		});
	}

	@Override
	public void clearCart(CustomerIdentifier identifier, List<Long> bookIds) {
		String cartHashKey = getCartHashKey(identifier.guestId());
		String cartSortedSetKey = getCartSortedSetKey(identifier.guestId());
		stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				RedisOperations<String, String> ops = operations;
				ops.multi();

				ops.opsForHash()
					.delete(cartHashKey, bookIds.stream().map(String::valueOf).toArray());
				ops.opsForZSet()
					.remove(cartSortedSetKey, bookIds.stream().map(String::valueOf).toArray());

				List<Object> result = ops.exec();
				log.info("주문 완료된 장바구니 상품 제거");
				return result;
			}
		});
	}

	private String getCartHashKey(String guestId) {
		return "cart:%s:qty".formatted(guestId);
	}

	private String getCartSortedSetKey(String guestId) {
		return "cart:%s:order".formatted(guestId);
	}

	private double generateScore() {
		return System.currentTimeMillis();
	}
}
