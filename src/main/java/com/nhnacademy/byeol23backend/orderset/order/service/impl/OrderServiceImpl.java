package com.nhnacademy.byeol23backend.orderset.order.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.domain.dto.BookOrderInfoResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookNotFoundException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.exception.MemberNotFoundException;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
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
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.PointOrderResponse;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.repository.OrderDetailRepository;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;
import com.nhnacademy.byeol23backend.orderset.packaging.exception.PackagingNotFoundException;
import com.nhnacademy.byeol23backend.orderset.packaging.repository.PackagingRepository;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import lombok.RequiredArgsConstructor;

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
	private final JwtParser jwtParser;
	private final PasswordEncoder passwordEncoder;
	private static final String ORDER_STATUS_PAYMENT_COMPLETED = "결제 완료";
	private static final String ORDER_STATUS_ORDER_CANCELED = "주문 취소";
	private static final String PAYMENT_METHOD_POINT = "포인트 결제";
	private static final String ORDER_NOT_FOUND_MESSAGE = "해당 주문 번호를 찾을 수 없습니다.: ";
	private static final String PAYMENT_NOT_FOUND_MESSAGE = "해당 결제를 찾을 수 없습니다.: ";
	private static final String DELIVERY_POLICY_NOT_FOUND_MESSAGE = "현재 배송 정책을 찾을 수 없습니다.";

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
			request.receiverPhone(), member, currentDeliveryPolicy);

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

		PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(request.cancelReason(),
			payment.getPaymentKey());

		paymentService.cancelPayment(paymentCancelRequest);

		updateOrderStatusToCanceled(order.getOrderId());

		return new OrderCancelResponse(order.getOrderNumber(), order.getActualOrderPrice(), order.getOrderStatus());
	}

	@Override
	public OrderDetailResponse getOrderByOrderNumber(String orderNumber) {
		Order order = orderRepository.findOrderByOrderNumber(orderNumber)
			.orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderNumber));

		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderWithBook(order);

		List<BookOrderInfoResponse> bookOrderInfoResponses = mapOrderDetailsToInfoResponses(orderDetails);

		return new OrderDetailResponse(order.getOrderNumber(), order.getOrderedAt(), order.getOrderStatus(),
			order.getActualOrderPrice(),
			order.getReceiver(), order.getReceiverPhone(), order.getReceiverAddress(), order.getReceiverAddressDetail(),
			order.getPostCode(), bookOrderInfoResponses);
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

		order.updateOrderStatus(ORDER_STATUS_PAYMENT_COMPLETED);

		return new PointOrderResponse(order.getOrderNumber(), order.getTotalBookPrice(), PAYMENT_METHOD_POINT);
	}

	@Override
	@Transactional
	public void updateBulkOrderStatus(OrderBulkUpdateRequest request) {
		orderRepository.updateOrderStatusByOrderNumbers(request.orderNumberLists(), request.status());
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
			.map(orderDetail -> new BookOrderInfoResponse(
				orderDetail.getBook().getBookName(),
				orderDetail.getQuantity(),
				orderDetail.getOrderPrice()
			))
			.toList();
	}

	private Long accessTokenParser(String accessToken) {
		return jwtParser.parseToken(accessToken).get("memberId", Long.class);
	}

}
