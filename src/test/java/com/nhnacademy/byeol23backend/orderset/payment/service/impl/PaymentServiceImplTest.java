package com.nhnacademy.byeol23backend.orderset.payment.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

import java.math.BigDecimal;
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

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.exception.BookStockNotEnoughException;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.order.exception.OrderNotFoundException;
import com.nhnacademy.byeol23backend.orderset.order.repository.OrderRepository;
import com.nhnacademy.byeol23backend.orderset.orderdetail.domain.OrderDetail;
import com.nhnacademy.byeol23backend.orderset.orderdetail.repository.OrderDetailRepository;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;
import com.nhnacademy.byeol23backend.orderset.payment.domain.PaymentProvider;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentConfirmResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentResultResponse;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentNotFoundException;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import com.nhnacademy.byeol23backend.pointset.pointhistories.service.PointService;

@Disabled
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

	@Mock
	private PaymentFacade paymentFacade;
	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OrderDetailRepository orderDetailRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private PointService pointService;

	@InjectMocks
	private PaymentServiceImpl paymentService;
	private static final String ORDER_NOT_FOUND_MESSAGE = "해당 주문을 찾을 수 없습니다.: ";

	// --- Mock Objects ---
	private Order mockOrder;
	private Member mockMember;
	private Payment mockPayment;
	private Book mockBook;
	private OrderDetail mockOrderDetail;
	private PointHistory mockPointHistory;

	// --- Test DTOs ---
	private PaymentParamRequest paramRequest;
	private PaymentConfirmResponse confirmResponse;
	private PaymentCancelRequest cancelRequest;
	private PaymentCancelResponse cancelResponse;
	private PaymentResultResponse resultResponse; // for createPayment

	@BeforeEach
	void setUp() {
		// Mock 객체 초기화
		mockOrder = Mockito.mock(Order.class);
		mockMember = Mockito.mock(Member.class);
		mockPayment = Mockito.mock(Payment.class);
		mockBook = Mockito.mock(Book.class);
		mockOrderDetail = Mockito.mock(OrderDetail.class);
		mockPointHistory = Mockito.mock(PointHistory.class);

		// DTO 초기화 (테스트 시나리오에 맞게 값 설정)
		paramRequest = new PaymentParamRequest("paymentKey123", "order123", new BigDecimal("15000"));

		confirmResponse = new PaymentConfirmResponse(
			"paymentKey123", "order123", "테스트 주문", "DONE", new BigDecimal("15000"),
			LocalDateTime.now().minusMinutes(1), LocalDateTime.now(), "TOSS_PAYMENTS"
		);

		resultResponse = new PaymentResultResponse(
			"order123", "paymentKey123", "테스트 주문", "DONE", new BigDecimal("15000"),
			LocalDateTime.now().minusMinutes(1), LocalDateTime.now(), "TOSS_PAYMENTS"
		);

		cancelRequest = new PaymentCancelRequest("단순 변심", "paymentKey123");

		cancelResponse = new PaymentCancelResponse("order123", "CANCELED");
	}

	@Test
	@DisplayName("결제 승인 - 회원 주문 (포인트 적립 O, setPointHistory O)")
	void confirmPayment_Success_MemberOrder() {
		// given
		given(orderRepository.findOrderByOrderNumber(paramRequest.orderId())).willReturn(Optional.of(mockOrder));
		given(paymentFacade.confirmPayment(PaymentProvider.TOSS_PAYMENTS, paramRequest)).willReturn(confirmResponse);
		given(orderDetailRepository.findAllByOrderWithBook(mockOrder)).willReturn(List.of(mockOrderDetail));

		given(mockOrderDetail.getBook()).willReturn(mockBook);
		given(mockBook.getBookId()).willReturn(1L);
		given(mockOrderDetail.getQuantity()).willReturn(2);

		given(bookRepository.decreaseBookStock(1L, 2)).willReturn(1); // 재고 차감 성공

		// [핵심] 회원 주문 설정
		given(mockOrder.getMember()).willReturn(mockMember);
		given(mockOrder.getActualOrderPrice()).willReturn(new BigDecimal("15000"));

		// [핵심] PointService가 PointHistory를 반환하도록 설정
		// given(pointService.offsetPointsByOrder(mockMember, new BigDecimal("15000"))).willReturn(mockPointHistory);

		// when
		PaymentResultResponse response = paymentService.confirmPayment(paramRequest);

		// then
		assertThat(response).isNotNull();
		assertThat(response.orderId()).isEqualTo("order123");
		assertThat(response.status()).isEqualTo("DONE");

		// 1. 주문 상태 업데이트 검증
		verify(mockOrder, times(1)).updateOrderStatus("결제 완료");
		// 2. 재고 차감 검증
		verify(bookRepository, times(1)).decreaseBookStock(1L, 2);
		// 3. 포인트 서비스 호출 검증 (회원)
		// verify(pointService, times(1)).offsetPointsByOrder(mockMember, new BigDecimal("15000"));
		// 4. [중요] Order 엔티티에 PointHistory가 설정되었는지 검증
		verify(mockOrder, times(1)).setPointHistory(mockPointHistory);
	}

	@Test
	@DisplayName("결제 승인 - 비회원 주문 (포인트 적립 X, setPointHistory X)")
	void confirmPayment_Success_NonMemberOrder() {
		// given
		given(orderRepository.findOrderByOrderNumber(paramRequest.orderId())).willReturn(Optional.of(mockOrder));
		given(paymentFacade.confirmPayment(PaymentProvider.TOSS_PAYMENTS, paramRequest)).willReturn(confirmResponse);
		given(orderDetailRepository.findAllByOrderWithBook(mockOrder)).willReturn(List.of(mockOrderDetail));

		given(mockOrderDetail.getBook()).willReturn(mockBook);
		given(mockBook.getBookId()).willReturn(1L);
		given(mockOrderDetail.getQuantity()).willReturn(2);

		given(bookRepository.decreaseBookStock(1L, 2)).willReturn(1);

		// [핵심] 비회원 주문 설정 (getMember()가 null 반환)
		given(mockOrder.getMember()).willReturn(null);

		// when
		PaymentResultResponse response = paymentService.confirmPayment(paramRequest);

		// then
		assertThat(response).isNotNull();
		assertThat(response.orderId()).isEqualTo("order123");

		// 1. 주문 상태 업데이트 검증
		verify(mockOrder, times(1)).updateOrderStatus("결제 완료");
		// 2. 재고 차감 검증
		verify(bookRepository, times(1)).decreaseBookStock(1L, 2);

		// 3. [중요] 비회원이므로 포인트 관련 로직은 절대 호출되면 안 됨
		// verify(pointService, never()).offsetPointsByOrder(any(), any());
		verify(mockOrder, never()).setPointHistory(any());
	}

	@Test
	@DisplayName("결제 승인 - 주문 조회 실패")
	void confirmPayment_OrderNotFound_ThrowsException() {
		// given
		given(orderRepository.findOrderByOrderNumber(paramRequest.orderId())).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> paymentService.confirmPayment(paramRequest))
			.isInstanceOf(OrderNotFoundException.class)
			.hasMessageContaining(ORDER_NOT_FOUND_MESSAGE + paramRequest.orderId());

		// (실패 시 이후 로직은 실행되지 않음)
		verify(paymentFacade, never()).confirmPayment(any(), any());
		verify(bookRepository, never()).decreaseBookStock(anyLong(), anyInt());
	}

	@Test
	@DisplayName("결제 승인 - 재고 차감 실패")
	void confirmPayment_StockDecreaseFails_ThrowsException() {
		// given
		given(orderRepository.findOrderByOrderNumber(paramRequest.orderId())).willReturn(Optional.of(mockOrder));
		given(paymentFacade.confirmPayment(PaymentProvider.TOSS_PAYMENTS, paramRequest)).willReturn(confirmResponse);
		given(orderDetailRepository.findAllByOrderWithBook(mockOrder)).willReturn(List.of(mockOrderDetail));

		given(mockOrderDetail.getBook()).willReturn(mockBook);
		given(mockBook.getBookId()).willReturn(1L);
		given(mockOrderDetail.getQuantity()).willReturn(2);

		// [핵심] 재고 차감 실패 (0 반환)
		given(bookRepository.decreaseBookStock(1L, 2)).willReturn(0);

		// when & then
		assertThatThrownBy(() -> paymentService.confirmPayment(paramRequest))
			.isInstanceOf(BookStockNotEnoughException.class)
			.hasMessageContaining("재고 차감 실패 : 1");

		// (비록 @Transactional로 롤백되겠지만, 서비스 로직상 호출은 됨)
		verify(mockOrder, times(1)).updateOrderStatus("결제 완료");
		// (포인트 로직은 재고 차감 실패 이전에 실행되지 않음)
		// verify(pointService, never()).offsetPointsByOrder(any(), any());
	}

	@Test
	@DisplayName("결제 취소 - 성공")
	void cancelPayment_Success() {
		// given
		given(paymentRepository.findPaymentByPaymentKey(cancelRequest.paymentKey())).willReturn(
			Optional.of(mockPayment));
		given(mockPayment.getOrder()).willReturn(mockOrder); // Payment -> Order 조회
		given(paymentFacade.cancelPayment(PaymentProvider.TOSS_PAYMENTS, cancelRequest)).willReturn(cancelResponse);

		// when
		PaymentCancelResponse response = paymentService.cancelPayment(cancelRequest);

		// then
		assertThat(response).isEqualTo(cancelResponse);
		verify(paymentFacade, times(1)).cancelPayment(PaymentProvider.TOSS_PAYMENTS, cancelRequest);
		verify(mockOrder, times(1)).updateOrderStatus("주문 취소");
	}

	@Test
	@DisplayName("결제 취소 - 결제(PaymentKey) 조회 실패")
	void cancelPayment_PaymentNotFound_ThrowsException() {
		// given
		given(paymentRepository.findPaymentByPaymentKey(cancelRequest.paymentKey())).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> paymentService.cancelPayment(cancelRequest))
			.isInstanceOf(PaymentNotFoundException.class)
			.hasMessageContaining("해당 결제키를 찾을 수 없습니다.: " + cancelRequest.paymentKey());

		verify(paymentFacade, never()).cancelPayment(any(), any());
		verify(mockOrder, never()).updateOrderStatus(anyString());
	}

	@Test
	@DisplayName("결제 정보 생성(Webhook) - 성공")
	void createPayment_Success() {
		// given
		given(orderRepository.findOrderByOrderNumber(resultResponse.orderId())).willReturn(Optional.of(mockOrder));

		// save()에 전달될 Payment 객체를 캡처
		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

		// when
		paymentService.createPayment(resultResponse);

		// then
		// 1. save가 1번 호출되었는지, 캡처되었는지
		verify(paymentRepository, times(1)).save(paymentCaptor.capture());

		// 2. 캡처된 Payment 객체의 필드가 DTO와 일치하는지 검증
		Payment savedPayment = paymentCaptor.getValue();
		assertThat(savedPayment.getPaymentKey()).isEqualTo(resultResponse.paymentKey());
		assertThat(savedPayment.getOrderName()).isEqualTo(resultResponse.orderName());
		assertThat(savedPayment.getPaymentMethod()).isEqualTo(resultResponse.method());
		assertThat(savedPayment.getTotalAmount()).isEqualTo(resultResponse.totalAmount());
		assertThat(savedPayment.getOrder()).isEqualTo(mockOrder);
	}
}