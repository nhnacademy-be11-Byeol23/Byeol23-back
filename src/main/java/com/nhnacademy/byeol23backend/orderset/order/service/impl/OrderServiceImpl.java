package com.nhnacademy.byeol23backend.orderset.order.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookOrderInfoResponse;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.dto.NonmemberOrderRequest;
import com.nhnacademy.byeol23backend.memberset.member.exception.MemberNotFoundException;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.exception.DeliveryPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.delivery.repository.DeliveryPolicyRepository;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderBulkUpdateRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCreateResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderDetailResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.PointOrderResponse;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderPasswordNotMatchException;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderTemporaryStorageException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.repository.OrderDetailRepository;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.exception.PackagingNotFoundException;
import com.nhnacademy.byeol23backend.orderset.packaging.repository.PackagingRepository;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;
import com.nhnacademy.byeol23backend.pointset.orderpoint.domain.OrderPoint;
import com.nhnacademy.byeol23backend.pointset.orderpoint.repository.OrderPointRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.exception.PointNotEnoughException;
import com.nhnacademy.byeol23backend.pointset.pointhistories.repository.PointHistoryRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.utils.JwtParser;
import com.nhnacademy.byeol23backend.utils.MemberUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final BookRepository bookRepository;
	private final PaymentRepository paymentRepository;
	private final PaymentService paymentService;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private final PackagingRepository packagingRepository;
	private final OrderPointRepository orderPointRepository;
	private final PointService pointService;
	private final JwtParser jwtParser;
	private final PasswordEncoder passwordEncoder;
	private static final String ORDER_STATUS_PAYMENT_COMPLETED = "결제 완료";
	private static final String ORDER_STATUS_ORDER_CANCELED = "주문 취소";
	private static final String PAYMENT_METHOD_POINT = "포인트 결제";
	private static final String ORDER_NOT_FOUND_MESSAGE = "해당 주문 번호를 찾을 수 없습니다.: ";
	private static final String PAYMENT_NOT_FOUND_MESSAGE = "해당 결제를 찾을 수 없습니다.: ";
	private static final String DELIVERY_POLICY_NOT_FOUND_MESSAGE = "현재 배송 정책을 찾을 수 없습니다.";
	private final PointHistoryRepository pointHistoryRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	@Transactional
	public OrderPrepareResponse prepareOrder(OrderPrepareRequest request, String accessToken) {
		String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String randomPart = String.format("%06d", new Random().nextInt(1_000_000));
		String orderId = timeStamp + randomPart;
		Long memberId;
		Member member = null;

		// 회원
		if (accessToken != null && !accessToken.isBlank()) {
			memberId = accessTokenParser(accessToken);
			member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberNotFoundException("해당 아이디의 멤버를 찾을 수 없습니다.: " + memberId));
		}

		String orderPassword = request.orderPassword() == null ? null : passwordEncoder.encode(request.orderPassword());

		DeliveryPolicy currentDeliveryPolicy = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()
			.orElseThrow(() -> new DeliveryPolicyNotFoundException(DELIVERY_POLICY_NOT_FOUND_MESSAGE));

		Order order = Order.of(orderId, orderPassword, request.totalBookPrice(), request.actualOrderPrice(),
			request.deliveryArrivedDate(), request.receiver(), request.postCode(),
			request.receiverAddress(), request.receiverAddressDetail(), request.receiverAddressExtra(),
			request.receiverPhone(), member, currentDeliveryPolicy, request.isCartCheckout());

		orderRepository.save(order);

		for (BookInfoRequest bookInfoRequest : request.bookInfoRequestList()) {
			Book book = bookRepository.findById(bookInfoRequest.bookId())
				.orElseThrow(() -> new BookNotFoundException("해당 아이디의 도서가 존재하지 않습니다.: " + bookInfoRequest.bookId()));

			Packaging packaging =
				bookInfoRequest.packagingId() != 0 ? packagingRepository.findById(bookInfoRequest.packagingId())
					.orElseThrow(
						() -> new PackagingNotFoundException(
							"해당 아이디의 포장지를 찾을 수 없습니다.: " + bookInfoRequest.packagingId())) : null;

			OrderDetail orderDetail = OrderDetail.of(bookInfoRequest.quantity(), book.getSalePrice(),
				book, packaging, order);

			orderDetailRepository.save(orderDetail);
		}

		// 회원 주문이고(member가 null이 아니고) 사용한 포인트량이 null이 아니면
		if (!Objects.isNull(member) && !Objects.isNull(request.usedPoints())) {
			BigDecimal usedPoints = request.usedPoints();
			BigDecimal currentPoint = member.getCurrentPoint();

			if (usedPoints.compareTo(currentPoint) > 0) {
				throw new PointNotEnoughException("보유 포인트를 초과하여 사용할 수 없습니다.");
			}

			if (usedPoints.compareTo(BigDecimal.ZERO) > 0) {
				PointHistory pointHistory = pointService.offsetPointsWithExtra(member, ReservedPolicy.ORDER,
					usedPoints.negate());
				order.setPointHistory(pointHistory);
				OrderPoint orderPoint = new OrderPoint(order, pointHistory);
				orderPointRepository.save(orderPoint);
			}
		}

		return new OrderPrepareResponse(order.getOrderNumber(), order.getActualOrderPrice(), order.getReceiver());
	}

	@Override
	@Transactional
	public OrderCreateResponse updateOrderStatus(String orderNumber, String orderStatus) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));

		order.updateOrderStatus(orderStatus);

		return new OrderCreateResponse(orderNumber, order.getTotalBookPrice(), order.getActualOrderPrice(),
			order.getOrderedAt(), order.getOrderStatus(), order.getReceiver(),
			order.getPostCode(), order.getReceiverAddress(), order.getReceiverAddressDetail(),
			order.getReceiverPhone());
	}

	@Override
	@Transactional
	public OrderCancelResponse cancelOrder(String orderNumber, OrderCancelRequest request) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));
		Payment payment = paymentRepository.findPaymentByOrder(order)
			.orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND_MESSAGE + order.getOrderNumber()));

		if (request.cancelReason().equals("고객 요청에 의한 취소")) {
			PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(request.cancelReason(),
				payment.getPaymentKey());

			paymentService.cancelPayment(paymentCancelRequest);
		}

		updateOrderStatusToCanceled(order.getOrderId());

		return new OrderCancelResponse(order.getOrderNumber(), order.getActualOrderPrice(), order.getOrderStatus());
	}

	@Override
	public OrderDetailResponse getOrderByOrderNumber(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));

		return mapOrderDetailsToOrderDetailResponse(order);
	}

	@Override
	public Page<OrderInfoResponse> searchOrders(OrderSearchCondition orderSearchCondition, Pageable pageable) {
		Page<OrderInfoResponse> resultPage = orderRepository.searchOrders(orderSearchCondition, pageable);

		return resultPage;
	}

	@Override
	@Transactional
	public PointOrderResponse createOrderWithPoints(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));

		PointHistory pointHistory = pointService.offsetPointsByOrder(order.getMember(), order.getActualOrderPrice());
		pointHistoryRepository.save(pointHistory);
		order.setPointHistory(pointHistory);

		OrderPoint orderPoint = new OrderPoint(order, pointHistory);
		orderPointRepository.save(orderPoint);

		order.updateOrderStatus(ORDER_STATUS_PAYMENT_COMPLETED);

		return new PointOrderResponse(order.getOrderNumber(), order.getTotalBookPrice(), PAYMENT_METHOD_POINT);
	}

	@Override
	@Transactional
	public void updateBulkOrderStatus(OrderBulkUpdateRequest request) {
		orderRepository.updateOrderStatusByOrderNumbers(request.orderNumberLists(), request.status());
	}

	@Override
	public Page<OrderDetailResponse> getOrders(Pageable pageable) {
		Long memberId = MemberUtil.getMemberId();

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("해당 아이디의 회원을 찾을 수 없습니다.: " + memberId));

		Page<Order> orderList = orderRepository.findByMemberAndOrderStatusNotOrderByOrderedAtDesc(member, "대기",
			pageable);

		return orderList.map(order -> {
			List<OrderDetail> orderDetailsForThisOrder = orderDetailRepository.findByOrder(order);

			DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(
					order.getDeliveryPolicy().getDeliveryPolicyId())
				.orElseThrow(() -> new DeliveryPolicyNotFoundException(
					"해당 아이디의 배달 정책을 찾을 수 없습니다.: " + order.getDeliveryPolicy().getDeliveryPolicyId()));

			DeliveryPolicyInfoResponse delivery = new DeliveryPolicyInfoResponse(
				deliveryPolicy.getFreeDeliveryCondition(),
				deliveryPolicy.getDeliveryFee(), deliveryPolicy.getChangedAt());

			BigDecimal usedPoints = BigDecimal.ZERO;

			if (order.getPointHistory() != null && order.getPointHistory().getPointHistoryId() != null) {
				PointHistory pointHistory = pointHistoryRepository.findById(order.getPointHistory().getPointHistoryId())
					.orElse(null);
				if (pointHistory != null && pointHistory.getPointAmount() != null) {
					usedPoints = pointHistory.getPointAmount();
				}
			}

			List<BookOrderInfoResponse> bookOrderInfos = orderDetailsForThisOrder.stream()
				.map(detail -> {
					Packaging packaging = detail.getPackaging();
					PackagingInfoResponse packagingResponse = null;

					if (packaging != null) {
						packagingResponse = new PackagingInfoResponse(
							packaging.getPackagingId(),
							packaging.getPackagingName(),
							packaging.getPackagingPrice(),
							packaging.getPackagingImgUrl()
						);
					}

					return new BookOrderInfoResponse(
						detail.getBook().getBookId(),
						detail.getBook().getBookName(),
						detail.getQuantity(),
						detail.getOrderPrice(),
						packagingResponse
					);
				})
				.toList();

			return new OrderDetailResponse(
				order.getOrderNumber(),
				order.getOrderedAt(),
				order.getOrderStatus(),
				order.getActualOrderPrice(),
				order.getReceiver(),
				order.getReceiverPhone(),
				order.getReceiverAddress(),
				order.getReceiverAddressDetail(),
				order.getPostCode(),
				bookOrderInfos, // <-- 해당 주문에 속한 상품 목록
				delivery,
				usedPoints
			);
		});
	}

	@Override
	public OrderDetailResponse getNonMemberOrder(NonmemberOrderRequest request) {
		Order order = orderRepository.findOrderByOrderNumber(request.orderNumber())
			.orElseThrow(() -> new OrderNotFoundException("해당 주문 번호의 주문을 찾을 수 없습니다.: " + request.orderNumber()));

		if (!verifyOrderPassword(request.orderPassword(), order.getOrderPassword())) {
			throw new OrderPasswordNotMatchException("주문 비밀번호가 일치하지 않습니다.");
		}

		return mapOrderDetailsToOrderDetailResponse(order);
	}

	@Override
	public String saveGuestOrderTmp(String guestId, OrderRequest orderRequest) {
		String validationToken = UUID.randomUUID().toString();

		String key = "GUEST_ORDER_TMP:" + guestId + ":" + validationToken;

		try {
			ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
			valueOperations.set(key, orderRequest, 10, TimeUnit.MINUTES);
		} catch (Exception e) {
			log.error("비회원 주문 임시 데이터 저장 실패: {}", e);
			throw new OrderTemporaryStorageException("비회원 주문 임시 데이터 Redis 저장에 실패했습니다.");
		}

		log.info("Saving order: guestId={}, validationToken={}", guestId, validationToken);

		return validationToken;
	}

	@Override
	public String saveMemberOrderTmp(Long memberId, OrderRequest orderRequest) {

		String validationToken = UUID.randomUUID().toString();

		String key = "MEMBER_ORDER_TMP:" + memberId + ":" + validationToken;

		try {
			ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
			valueOperations.set(key, orderRequest, 10, TimeUnit.MINUTES);
		} catch (Exception e) {
			log.error("회원 주문 임시 데이터 저장 실패: {}", e);
			throw new OrderTemporaryStorageException("회원 주문 임시 데이터 Redis 저장에 실패했습니다.");
		}

		log.info("회원 주문 데이터가 Redis에 임시 저장되었습니다. Token:{}", validationToken);

		return validationToken;
	}

	@Override
	public OrderRequest getAndRemoveOrderRequest(String validationToken) {

		String guestKeyPattern = "GUEST_ORDER_TMP:*:" + validationToken;
		String memberKeyPattern = "MEMBER_ORDER_TMP:*:" + validationToken;

		String actualKey = null;

		Set<String> guestKeys = redisTemplate.keys(guestKeyPattern);
		if (guestKeys != null && !guestKeys.isEmpty()) {
			actualKey = guestKeys.iterator().next();
		}

		if (actualKey == null) {
			Set<String> memberKeys = redisTemplate.keys(memberKeyPattern);
			if (memberKeys != null && !memberKeys.isEmpty()) {
				actualKey = memberKeys.iterator().next();
			}
		}

		if (actualKey == null) {
			log.warn("OrderRequest data not found for token: {}", validationToken);
			return null;
		}

		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		Object rawDto = valueOperations.get(actualKey);

		redisTemplate.delete(actualKey);

		if (rawDto == null) {
			return null;
		}

		return objectMapper.convertValue(rawDto, OrderRequest.class);
	}

	@Override
	@Transactional
	public String migrateGuestOrderToMember(String token, String validationToken) {
		Long memberId = accessTokenParser(token);

		String guestKey = "GUEST_ORDER_TMP:*:" + validationToken;
		String memberKey = "MEMBER_ORDER_TMP:" + memberId + ":" + validationToken;

		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

		Object rawDto = valueOperations.get(guestKey);

		if (rawDto == null) {
			log.warn("Guest order data not found or expired for token: {}", validationToken);
			return validationToken;
		}

		redisTemplate.delete(guestKey);

		valueOperations.set(memberKey, rawDto, 10, TimeUnit.MINUTES);

		log.info("주문 데이터가 비회원({})에서 회원({})으로 성공적으로 마이그레이션되었습니다.", guestKey, memberKey);

		return validationToken;
	}

	@Transactional
	public void updateOrderStatusToCanceled(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderId));

		order.updateOrderStatus(ORDER_STATUS_ORDER_CANCELED);
	}

	@Transactional(readOnly = true)
	protected List<BookOrderInfoResponse> mapOrderDetailsToInfoResponses(List<OrderDetail> orderDetails) {
		return orderDetails.stream()
			.map(orderDetail -> {
					Packaging packaging = orderDetail.getPackaging();
					PackagingInfoResponse packagingInfoResponse = null;

					if (!Objects.isNull(packaging)) {
						packagingInfoResponse = new PackagingInfoResponse(
							packaging.getPackagingId(),
							packaging.getPackagingName(),
							packaging.getPackagingPrice(),
							packaging.getPackagingImgUrl()
						);
					}

					return new BookOrderInfoResponse(
						orderDetail.getBook().getBookId(),
						orderDetail.getBook().getBookName(),
						orderDetail.getQuantity(),
						orderDetail.getOrderPrice(),
						packagingInfoResponse
					);
				}
			)
			.toList();
	}

	@Transactional(readOnly = true)
	protected OrderDetailResponse mapOrderDetailsToOrderDetailResponse(Order order) {
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderWithBook(order);

		List<BookOrderInfoResponse> bookOrderInfoResponses = mapOrderDetailsToInfoResponses(orderDetails);

		DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(
				order.getDeliveryPolicy().getDeliveryPolicyId())
			.orElseThrow(() -> new DeliveryPolicyNotFoundException(
				"해당 아이디의 배달 정책을 찾을 수 없습니다.: " + order.getDeliveryPolicy().getDeliveryPolicyId()));
		DeliveryPolicyInfoResponse delivery = new DeliveryPolicyInfoResponse(deliveryPolicy.getFreeDeliveryCondition(),
			deliveryPolicy.getDeliveryFee(), deliveryPolicy.getChangedAt());

		// 포인트 amount가 0보다 작은 값을 가져옴
		BigDecimal pointAmount = orderPointRepository.findUsedPointsAmountByOrder(order);

		// 포인트 내역에서 포인트 사용량이 있고, 포인트 사용량이 0보다 낮으면 orderDetail에 저장
		BigDecimal usedPoints = pointAmount != null ? pointAmount.abs() : BigDecimal.ZERO;

		return new OrderDetailResponse(order.getOrderNumber(), order.getOrderedAt(), order.getOrderStatus(),
			order.getActualOrderPrice(), order.getReceiver(), order.getReceiverPhone(), order.getReceiverAddress(),
			order.getReceiverAddressDetail(), order.getPostCode(), bookOrderInfoResponses,
			delivery, usedPoints);
	}

	private Long accessTokenParser(String accessToken) {
		return jwtParser.parseToken(accessToken).get("memberId", Long.class);
	}

	private boolean verifyOrderPassword(String rawPassword, String storedHash) {
		return passwordEncoder.matches(rawPassword, storedHash);
	}

}
