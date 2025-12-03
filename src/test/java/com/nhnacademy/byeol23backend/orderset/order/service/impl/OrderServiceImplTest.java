package com.nhnacademy.byeol23backend.orderset.order.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.exception.MemberNotFoundException;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.delivery.repository.DeliveryPolicyRepository;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderBulkUpdateRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderCancelRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderDetailResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderInfoResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderSearchCondition;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.repository.OrderDetailRepository;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;
import com.nhnacademy.byeol23backend.orderset.packaging.repository.PackagingRepository;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;

/**
 * method symbol이랑 다른 부분은 주석처리 해놨음
 */
@ExtendWith(MockitoExtension.class)
@Disabled
class OrderServiceImplTest {

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OrderDetailRepository orderDetailRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private PaymentService paymentService;
	@Mock
	private DeliveryPolicyRepository deliveryPolicyRepository;
	@Mock
	private PackagingRepository packagingRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private OrderServiceImpl orderServiceImpl;

	// --- Mock Objects ---
	private Member mockMember;
	private Book mockBook;
	private Packaging mockPackaging;
	private DeliveryPolicy mockPolicy;
	private Order mockOrder;
	private Payment mockPayment;
	private OrderPrepareRequest memberRequest;
	private BookInfoRequest bookInfoRequestWithPackaging;
	private BookInfoRequest bookInfoRequestWithoutPackaging;
	private static final String ORDER_STATUS_PAYMENT_COMPLETED = "결제 완료";
	private static final String ORDER_STATUS_ORDER_CANCELED = "주문 취소";
	private static final String ORDER_NOT_FOUND_MESSAGE = "해당 주문 번호를 찾을 수 없습니다.: ";
	private static final String PAYMENT_NOT_FOUND_MESSAGE = "해당 결제를 찾을 수 없습니다.: ";

	@BeforeEach
	void setUp() {
		// 공통 Mock 객체 초기화
		mockMember = Mockito.mock(Member.class);
		mockBook = Mockito.mock(Book.class);
		mockPackaging = Mockito.mock(Packaging.class);
		mockPolicy = Mockito.mock(DeliveryPolicy.class);
		mockOrder = Mockito.mock(Order.class);
		mockPayment = Mockito.mock(Payment.class);

		// BookInfoRequest (포장 O)
		bookInfoRequestWithPackaging = new BookInfoRequest(
			1L, "Test Book", "image.jpg", true,
			new BigDecimal("10000"), new BigDecimal("9000"),
			null, 2, null, 10L
		);
		// BookInfoRequest (포장 X, packagingId = 0L)
		bookInfoRequestWithoutPackaging = new BookInfoRequest(
			2L, "Test Book 2", "image2.jpg", true,
			new BigDecimal("12000"), new BigDecimal("11000"),
			null, 1, null, 0L
		);

		// 회원 주문 DTO
		memberRequest = new OrderPrepareRequest(
			new BigDecimal("29000"), new BigDecimal("25000"),
			"홍길동", "12345", "주소", "상세주소", null, "01012345678",
			LocalDate.now().plusDays(1),
			List.of(bookInfoRequestWithPackaging, bookInfoRequestWithoutPackaging),
			null // 회원 주문은 비밀번호가 null
		);

	}

	@Test
	@DisplayName("회원 주문 준비 (prepareOrder) 성공")
	void prepareOrder_Member_Success() {
		// given
		Long memberId = 1L;

		given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));
		given(deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()).willReturn(Optional.of(mockPolicy));

		// book 1 (포장 O)
		given(bookRepository.findById(1L)).willReturn(Optional.of(mockBook));
		given(packagingRepository.findById(10L)).willReturn(Optional.of(mockPackaging));
		// book 2 (포장 X)
		given(bookRepository.findById(2L)).willReturn(Optional.of(mockBook));
		// packagingRepository.findById(0L)는 호출되지 않아야 함 (서비스 로직에서 0L이면 null로 처리)

		// Order.of()는 정적 메서드이므로, save()에 넘어오는 Order 객체를 캡처
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		given(orderRepository.save(orderCaptor.capture())).willReturn(mockOrder);

		// when
		OrderPrepareResponse response = orderServiceImpl.prepareOrder(memberId, memberRequest);

		// then
		verify(memberRepository, times(1)).findById(memberId);
		verify(passwordEncoder, never()).encode(anyString()); // 회원 주문 시 비밀번호 암호화 X
		verify(orderRepository, times(1)).save(any(Order.class));
		verify(orderDetailRepository, times(2)).save(any(OrderDetail.class));
		verify(packagingRepository, times(1)).findById(10L); // 포장 10L만 조회

		Order savedOrder = orderCaptor.getValue();
		assertThat(savedOrder.getMember()).isEqualTo(mockMember);
		assertThat(savedOrder.getOrderPassword()).isNull();

		assertThat(response).isNotNull();
		assertThat(response.receiver()).isEqualTo("홍길동");
	}

	@Test
	@DisplayName("주문 준비 시 회원 조회 실패")
	void prepareOrder_MemberNotFound_ThrowsException() {
		// given
		Long memberId = 99L;
		given(memberRepository.findById(memberId)).willReturn(Optional.empty()); // 회원 없음

		// when & then
		assertThatThrownBy(() -> orderServiceImpl.prepareOrder(memberId, memberRequest))
			.isInstanceOf(MemberNotFoundException.class)
			.hasMessageContaining("해당 아이디의 멤버를 찾을 수 없습니다.: 99");
	}

	@Test
	@DisplayName("주문 상태 변경")
	void updateOrderStatus_Success() {
		// given
		String orderNumber = "test-order-123";
		String newStatus = "SHIPPED";
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));

		// when
		orderServiceImpl.updateOrderStatus(orderNumber, newStatus);

		// then
		verify(orderRepository, times(1)).findOrderByOrderNumber(orderNumber);
		verify(mockOrder, times(1)).updateOrderStatus(newStatus);
	}

	@Test
	@DisplayName("주문 상태 변경 시 주문 없음")
	void updateOrderStatus_OrderNotFound_ThrowsException() {
		// given
		String orderNumber = "non-existing-order";
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderServiceImpl.updateOrderStatus(orderNumber, "SHIPPED"))
			.isInstanceOf(OrderNotFoundException.class)
			.hasMessageContaining(ORDER_NOT_FOUND_MESSAGE + orderNumber);
	}

	@Test
	@DisplayName("주문 취소")
	void cancelOrder_Success() {
		// given
		String orderNumber = "test-order-123";
		String expectedReason = "고객 요청에 의한 취소";
		OrderCancelRequest cancelRequest = new OrderCancelRequest(expectedReason);

		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));
		given(paymentRepository.findPaymentByOrder(mockOrder)).willReturn(Optional.of(mockPayment));
		given(mockPayment.getPaymentKey()).willReturn("paymentKey123");

		given(paymentService.cancelPayment(any(PaymentCancelRequest.class))).willReturn(null);

		// updateOrderStatusToCanceled 내부에서 findById 호출
		given(mockOrder.getOrderId()).willReturn(1L);
		given(orderRepository.findById(1L)).willReturn(Optional.of(mockOrder));

		// when
		orderServiceImpl.cancelOrder(orderNumber, cancelRequest);

		// then
		verify(paymentService, times(1)).cancelPayment(any(PaymentCancelRequest.class));
		verify(orderRepository, times(1)).findById(1L); // updateOrderStatusToCanceled 호출 검증
		verify(mockOrder, times(1)).updateOrderStatus(ORDER_STATUS_ORDER_CANCELED);
	}

	@Test
	@DisplayName("주문 취소 시 결제 내역 없음")
	void cancelOrder_PaymentNotFound_ThrowsException() {
		// given
		String orderNumber = "test-order-123";
		OrderCancelRequest cancelRequest = new OrderCancelRequest("MIND_CHANGED");
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));
		given(paymentRepository.findPaymentByOrder(mockOrder)).willReturn(Optional.empty()); // 결제 내역 없음

		// when & then
		assertThatThrownBy(() -> orderServiceImpl.cancelOrder(orderNumber, cancelRequest))
			.isInstanceOf(PaymentNotFoundException.class)
			.hasMessageContaining(PAYMENT_NOT_FOUND_MESSAGE);
	}

	@Test
	@DisplayName("주문 상세 조회")
	void getOrderByOrderNumber_Success() {
		// given
		String orderNumber = "test-order-123";
		DeliveryPolicy mockDeliveryPolicy = Mockito.mock(DeliveryPolicy.class);
		final Long POLICY_ID = 5L;
		final BigDecimal DELIVERY_FEE = new BigDecimal("3000");

		given(mockDeliveryPolicy.getDeliveryPolicyId()).willReturn(POLICY_ID);
		given(mockDeliveryPolicy.getDeliveryFee()).willReturn(DELIVERY_FEE);

		Order mockOrder = Mockito.mock(Order.class);
		given(mockOrder.getOrderNumber()).willReturn(orderNumber);

		given(mockOrder.getDeliveryPolicy()).willReturn(mockDeliveryPolicy);

		// Mock OrderDetail 및 Book
		OrderDetail mockDetail1 = Mockito.mock(OrderDetail.class);
		Book mockBook1 = Mockito.mock(Book.class);
		given(mockDetail1.getBook()).willReturn(mockBook1);
		given(mockBook1.getBookName()).willReturn("Test Book");
		given(mockDetail1.getQuantity()).willReturn(2);
		given(mockDetail1.getOrderPrice()).willReturn(new BigDecimal("9000"));

		List<OrderDetail> details = List.of(mockDetail1);
		given(deliveryPolicyRepository.findById(POLICY_ID)).willReturn(Optional.of(mockDeliveryPolicy));
		// 5. orderRepository 설정
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));

		// 6. orderDetailRepository 설정
		given(orderDetailRepository.findAllByOrderWithBook(mockOrder)).willReturn(details);

		// when (실행)
		OrderDetailResponse response = orderServiceImpl.getOrderByOrderNumber(orderNumber);

		// then
		assertThat(response).isNotNull();
		assertThat(response.items()).hasSize(1);
		assertThat(response.items().get(0).bookTitle()).isEqualTo("Test Book");
		assertThat(response.items().get(0).quantity()).isEqualTo(2);
	}

	@Test
	@DisplayName("주문 목록 검색")
	void searchOrders_Success() {
		// given
		OrderSearchCondition condition = new OrderSearchCondition("ORDERED", null, null);
		Pageable pageable = PageRequest.of(0, 10);
		OrderInfoResponse infoResponse = new OrderInfoResponse("test-order-123", LocalDateTime.now(), "홍길동",
			BigDecimal.TEN, "ORDERED");
		Page<OrderInfoResponse> mockPage = new PageImpl<>(List.of(infoResponse), pageable, 1);

		given(orderRepository.searchOrders(condition, pageable)).willReturn(mockPage);

		// when
		Page<OrderInfoResponse> result = orderServiceImpl.searchOrders(condition, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).orderNumber()).isEqualTo("test-order-123");
	}

	@Test
	@DisplayName("포인트로 주문 생성")
	void createOrderWithPoints_Success() {
		// given
		String orderNumber = "test-order-123";
		given(orderRepository.findOrderByOrderNumber(orderNumber)).willReturn(Optional.of(mockOrder));

		// when
		orderServiceImpl.createOrderWithPoints(orderNumber);

		// then
		verify(orderRepository, times(1)).findOrderByOrderNumber(orderNumber);
		verify(mockOrder, times(1)).updateOrderStatus(ORDER_STATUS_PAYMENT_COMPLETED);
	}

	@Test
	@DisplayName("주문 상태 일괄 변경")
	void updateBulkOrderStatus_Success() {
		// given
		List<String> orderNumbers = List.of("order1", "order2");
		String newStatus = "SHIPPED";
		OrderBulkUpdateRequest request = new OrderBulkUpdateRequest(orderNumbers, newStatus);

		// void 메서드는 doNothing()을 사용하거나, Mockito가 기본으로 처리하도록 둡니다.
		// BDD 스타일
		// willDoNothing().given(orderRepository).updateOrderStatusByOrderNumbers(orderNumbers, newStatus);

		// when
		orderServiceImpl.updateBulkOrderStatus(request);

		// then
		verify(orderRepository, times(1)).updateOrderStatusByOrderNumbers(orderNumbers, newStatus);
	}
}