package com.nhnacademy.byeol23backend.orderset.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.domain.dto.BookOrderInfoResponse;
import com.nhnacademy.byeol23backend.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23backend.config.SecurityConfig;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;
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
import com.nhnacademy.byeol23backend.orderset.order.service.OrderService;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;

@Disabled
@WebMvcTest(value = OrderController.class,
	excludeFilters = @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class}))
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtParser jwtParser;

	@Autowired
	private ObjectMapper objectMapper; // JSON 직렬화/역직렬화를 위해

	@MockBean
	private OrderService orderService; // 컨트롤러가 의존하는 서비스 Mock

	@MockBean
	private MemberRepository memberRepository;

	private String testOrderNumber;
	private BigDecimal totalBookPrice;
	private BigDecimal actualOrderPrice;
	private String receiver;
	private String postCode;
	private String receiverAddress;
	private String receiverAddressDetail;
	private String receiverAddressExtra;
	private String receiverPhone;
	private LocalDate deliveryArrivedDate;
	private List<BookInfoRequest> bookInfoRequestList;
	private String orderPassword;
	private List<BookOrderInfoResponse> items;
	private OrderPrepareResponse prepareResponse;
	private OrderPrepareRequest prepareRequest;

	@BeforeEach
	void setUp() {
		// 테스트 전반에 사용될 공통 변수 초기화
		testOrderNumber = "123456789012345678";
		totalBookPrice = new BigDecimal("30000");
		actualOrderPrice = new BigDecimal("25000");
		receiver = "테스트 수신자";
		postCode = "12345";
		receiverAddress = "테스트 주소";
		receiverAddressDetail = "101호";
		receiverAddressExtra = "(테스트동)";
		receiverPhone = "010-1234-5678";
		deliveryArrivedDate = LocalDate.now().plusDays(3);
		bookInfoRequestList = List.of();
		orderPassword = "testPassword123!";
		prepareResponse = new OrderPrepareResponse(testOrderNumber, actualOrderPrice, receiver);
		// OrderPrepareRequest는 DTO 구조를 모르므로 null로 두거나, 필요시 dummy 생성
		prepareRequest = new OrderPrepareRequest(
			totalBookPrice,
			actualOrderPrice,
			receiver,
			postCode,
			receiverAddress,
			receiverAddressDetail,
			receiverAddressExtra,
			receiverPhone,
			deliveryArrivedDate,
			bookInfoRequestList,
			orderPassword
		);
		// 1. [수정] Mockito.mock(Claims.class)를 다시 사용
		Claims mockClaims = Mockito.mock(Claims.class);

		// 2. [수정] 모든 인자에 eq() 매처를 사용하여 'get' 메서드를 정확하게 모킹
		given(mockClaims.get(eq("memberId"), eq(Long.class))).willReturn(1L);

		// 3. parseToken()이 "member-access-token"으로 호출되면 'mockClaims' 반환
		given(jwtParser.parseToken(eq("member-access-token"))).willReturn(mockClaims);

		// 4. MemberRepository가 1L로 호출되면 가짜 Member 객체 반환
		Member mockMember = Mockito.mock(Member.class);
		given(memberRepository.getReferenceById(anyLong())).willReturn(mockMember);

		// 5. TokenFilter가 'mockMember.getMemberId()'를 호출할 때 1L 반환 (NPE 방지)
		given(mockMember.getMemberId()).willReturn(1L);
	}

	@Test
	@DisplayName("POST /api/orders (주문 준비) - 회원")
	void prepareOrder_Member_Success() throws Exception {
		// given
		given(orderService.prepareOrder(any(), any(OrderPrepareRequest.class))).willReturn(prepareResponse);

		// when & then
		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(prepareRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.orderNumber").value(testOrderNumber));

		verify(orderService, times(1)).prepareOrder(any(), any(OrderPrepareRequest.class));
	}

	@Test
	@DisplayName("POST /api/orders (주문 준비) - 비회원")
	void prepareOrder_NonMember_Success() throws Exception {
		// given
		// 비회원은 accessToken이 null로 전달됨
		given(orderService.prepareOrder(eq(null), any(OrderPrepareRequest.class))).willReturn(prepareResponse);

		// when & then
		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(prepareRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.orderNumber").value(testOrderNumber));

		verify(orderService, times(1)).prepareOrder( eq(null), any(OrderPrepareRequest.class));
	}

	@Test
	@DisplayName("PUT /api/orders (주문 상태 변경)")
	void updateOrderStatus_Success() throws Exception {
		// given
		String newStatus = "주문 취소";
		OrderCreateResponse response = new OrderCreateResponse(testOrderNumber,
			totalBookPrice,
			actualOrderPrice,
			LocalDateTime.now(),
			"주문 취소",
			receiver,
			postCode,
			receiverAddress,
			receiverAddressDetail,
			receiverPhone);
		given(orderService.updateOrderStatus(eq(testOrderNumber), eq(newStatus))).willReturn(response);

		// when & then
		mockMvc.perform(put("/api/orders")
				.param("orderNumber", testOrderNumber)
				.param("orderStatus", newStatus))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.orderNumber").value(testOrderNumber))
			.andExpect(jsonPath("$.orderStatus").value(newStatus));

		verify(orderService, times(1)).updateOrderStatus(testOrderNumber, newStatus);
	}

	@Test
	@DisplayName("POST /api/orders/{order-number} (주문 취소)")
	void cancelOrder_Success() throws Exception {
		// given
		OrderCancelRequest cancelRequest = new OrderCancelRequest("MIND_CHANGED");
		OrderCancelResponse response = new OrderCancelResponse(testOrderNumber,
			actualOrderPrice,
			"주문 취소");
		given(orderService.cancelOrder(eq(testOrderNumber), any(OrderCancelRequest.class))).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/orders/{order-number}", testOrderNumber)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cancelRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.orderNumber").value(testOrderNumber))

			// ▼▼▼ [수정] "$.status" -> "$.orderStatus" ▼▼▼
			.andExpect(jsonPath("$.orderStatus").value("주문 취소"));

		verify(orderService, times(1)).cancelOrder(eq(testOrderNumber), any(OrderCancelRequest.class));
	}

	@Test
	@DisplayName("GET /api/orders/{order-number} (주문 상세 조회)")
	void getOrderByOrderNumber_Success() throws Exception {
		// DeliveryPolicyInfoResponse (테스트용)
		DeliveryPolicyInfoResponse mockDeliveryPolicy = new DeliveryPolicyInfoResponse(
			new BigDecimal("50000.00"), // freeDeliveryCondition
			new BigDecimal("3000.00"),  // deliveryFee
			LocalDateTime.now()         // changedAt
		);

		// PackagingInfoResponse가 필요하다면 이와 같이 Mock 객체를 생성해야 합니다.
		// BookOrderInfoResponse의 5번째 인자(Packaging)가 null인 경우를 대비하여 Mock 객체 생성
		PackagingInfoResponse mockPackaging = new PackagingInfoResponse(
			1L, "기본 포장", new BigDecimal("0.00"), "default-url"
		);

		// BookOrderInfoResponse 리스트 (테스트용)
		List<BookOrderInfoResponse> mockItems = List.of(
			new BookOrderInfoResponse(100L, "테스트 도서 A", 2, new BigDecimal("4500.00"), mockPackaging)
		);

		// given
		// OrderDetailResponse DTO 구조에 맞춰 12개 인자를 모두 전달하도록 수정
		OrderDetailResponse response = new OrderDetailResponse(
			testOrderNumber,
			LocalDateTime.now(),
			"결제 완료",
			actualOrderPrice, // 이미 정의된 변수 사용 가정
			receiver,         // 이미 정의된 변수 사용 가정
			receiverPhone,    // 이미 정의된 변수 사용 가정
			receiverAddress,  // 이미 정의된 변수 사용 가정
			receiverAddressDetail, // 이미 정의된 변수 사용 가정
			postCode,         // 이미 정의된 변수 사용 가정
			mockItems,        // List<BookOrderInfoResponse>
			mockDeliveryPolicy, // DeliveryPolicyInfoResponse 추가
			new BigDecimal(3000) // usedPoints
		);

		// given(orderService.getOrderByOrderNumber(...))를 사용하셨으므로, Mocking을 유지합니다.
		given(orderService.getOrderByOrderNumber(eq(testOrderNumber))).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/orders/{order-number}", testOrderNumber)
				// 응답 타입을 JSON으로 명시적으로 요청합니다.
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.orderNumber").value(testOrderNumber)); // JSON 응답 검증

		verify(orderService, times(1)).getOrderByOrderNumber(testOrderNumber);
	}

	@Test
	@DisplayName("GET /api/orders (주문 목록 검색/조회)")
	void searchOrders_Success() throws Exception {
		// given
		OrderInfoResponse orderInfo = new OrderInfoResponse(testOrderNumber,
			LocalDateTime.now(),
			receiver,
			actualOrderPrice,
			"결제 완료");

		Pageable pageable = PageRequest.of(0, 10);
		Page<OrderInfoResponse> responsePage = new PageImpl<>(List.of(orderInfo), pageable, 1);

		given(orderService.searchOrders(any(OrderSearchCondition.class), any(Pageable.class))).willReturn(responsePage);

		// when & then
		mockMvc.perform(get("/api/orders")
				.param("page", "0")
				.param("size", "10")
				.param("orderStatus", "SHIPPING")) // OrderSearchCondition 필드
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].orderNumber").value(testOrderNumber))
			.andExpect(jsonPath("$.totalElements").value(1));

		verify(orderService, times(1)).searchOrders(any(OrderSearchCondition.class), any(Pageable.class));
	}

	@Test
	@DisplayName("POST /api/orders/points (포인트로 주문 생성)")
	void saveOrderWithPoints_Success() throws Exception {
		// given
		PointOrderResponse response = new PointOrderResponse(testOrderNumber, totalBookPrice, "포인트");
		given(orderService.createOrderWithPoints(eq(testOrderNumber))).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/orders/points")
				.param("orderNumber", testOrderNumber))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "/api/orders/points/" + testOrderNumber))
			.andExpect(jsonPath("$.orderNumber").value(testOrderNumber));

		verify(orderService, times(1)).createOrderWithPoints(testOrderNumber);
	}

	@Test
	@DisplayName("POST /api/orders/bulk-status (주문 상태 일괄 변경)")
	void updateBulkOrderStatus_Success() throws Exception {
		// given
		OrderBulkUpdateRequest request = new OrderBulkUpdateRequest(List.of("1", "2"), "SHIPPED");
		// void 메서드이므로 given.willDoNothing()을 사용 (또는 생략)

		// when & then
		mockMvc.perform(post("/api/orders/bulk-status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		verify(orderService, times(1)).updateBulkOrderStatus(any(OrderBulkUpdateRequest.class));
	}
}