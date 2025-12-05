package com.nhnacademy.byeol23backend.couponset.coupon.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import com.nhnacademy.byeol23backend.config.CouponBirthdayRabbitProperties;
import com.nhnacademy.byeol23backend.config.CouponBulkRabbitProperties;
import com.nhnacademy.byeol23backend.config.CouponIssueRabbitProperties;
import com.nhnacademy.byeol23backend.couponset.coupon.domain.Coupon;
import com.nhnacademy.byeol23backend.couponset.coupon.dto.*;
import com.nhnacademy.byeol23backend.couponset.coupon.repository.CouponRepository;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponCalculationStrategy;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponService;
import com.nhnacademy.byeol23backend.couponset.coupon.service.CouponValidationStrategy;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import com.nhnacademy.byeol23backend.utils.JwtParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
    private final RabbitTemplate rabbitTemplate;
    private final CouponIssueRabbitProperties couponIssueRabbitProperties;
    private final CouponBulkRabbitProperties couponBulkRabbitProperties;
    private final CouponBirthdayRabbitProperties couponBirthdayRabbitProperties;
    private final CategoryRepository categoryRepository;
    private final CouponRepository couponRepository;
    private final BookRepository bookRepository;
    private final JwtParser jwtParser;
    private final Map<String, CouponValidationStrategy> validationStrategyMap;
    private final Map<String, CouponCalculationStrategy> calculationStrategyMap;

    @Override
    public void sendIssueRequestToMQ(CouponIssueRequestDto request) {
        rabbitTemplate.convertAndSend(
                couponIssueRabbitProperties.exchange(),
                couponBulkRabbitProperties.routingKey(),
                request
        );
    }

    @Override
    public void sendBirthdayIssueRequestToMQ(BirthdayCouponIssueRequestDto request) {
        rabbitTemplate.convertAndSend(
                couponIssueRabbitProperties.exchange(),
                couponBirthdayRabbitProperties.routingKey(),
                request
        );
    }

    @Override
    @Transactional
    public void issueCoupon(CouponIssueRequestDto request) {
        int result = couponRepository.issueCouponToAllUsers(request.couponPolicyId(), request.couponName(), request.expiredDate());
        if (result <= 0) {
            throw new RuntimeException("쿠폰 발급 실패");
        }
    }

    @Override
    @Transactional
    public void issueBirthdayCoupon(BirthdayCouponIssueRequestDto request) {
        Long memberId = request.memberId();
        Long policyId = request.birthDateCouponPolicyId();

        boolean alreadyIssued = couponRepository.existsByMember_memberIdAndCouponPolicy_couponPolicyId(memberId, policyId);

        if (alreadyIssued) {
            log.info("[생일 쿠폰 발급] 이미 발급된 쿠폰입니다. MemberID: " + memberId);
            return;
        }

        int issuedCount = couponRepository.issueBirthdayCoupon(request.birthDateCouponPolicyId(), request.couponName(), request.memberId(), request.expiredDate());

        //발급 실패 시 예외 처리도 해야함 (DB 제약조건 오류 등)
        if (issuedCount != 1) {
            // 단일 사용자 발급이 실패하면 RuntimeException을 던져 MQ에 롤백/재처리 신호 필요
            throw new RuntimeException("생일 쿠폰 발급 실패: MemberID=" + memberId);
        }

        log.info("생일 쿠폰 발급 성공, MemberID: " + memberId);
    }

    @Override
    public List<IssuedCouponInfoResponseDto> getIssuedCoupons(String token) {
        Long memberId = accessTokenParser(token);
        List<Coupon> couponList = couponRepository.findByMember_MemberIdAndUsedAtIsNull(memberId);

        LocalDate today = LocalDate.now();

        // 3. Entity -> DTO 변환
        return couponList.stream()
                .map(coupon -> {
                    CouponPolicy policy = coupon.getCouponPolicy();

                    String discountStr;
                    if (policy.getDiscountAmount() != null) {
                        discountStr = policy.getDiscountAmount() + "원";
                    } else {
                        discountStr = policy.getDiscountRate() + "%";
                    }

                    boolean isValid = !today.isAfter(coupon.getExpiredDate());

                    return new IssuedCouponInfoResponseDto(
                            policy.getCouponPolicyId(),
                            coupon.getCouponId(),
                            coupon.getCouponName(),
                            policy.getCouponPolicyType().getValue(),
                            discountStr,
                            policy.getDiscountLimit(),
                            policy.getCriterionPrice(),
                            coupon.getCreatedDate(),
                            coupon.getExpiredDate(),
                            isValid
                    );
                })
                .toList();
    }

    @Override
    public List<UsedCouponInfoResponseDto> getUsedCoupons(String token) {
        Long memberId = accessTokenParser(token);

        List<Coupon> couponList = couponRepository.findByMember_MemberIdAndUsedAtIsNotNull(memberId);

        return couponList.stream()
                .map(coupon -> {
                    CouponPolicy policy = coupon.getCouponPolicy();

                    String discountStr;
                    if (policy.getDiscountAmount() != null) {
                        discountStr = policy.getDiscountAmount() + "원";
                    } else {
                        discountStr = policy.getDiscountRate() + "%";
                    }

                    return new UsedCouponInfoResponseDto(
                            policy.getCouponPolicyId(),
                            coupon.getCouponId(),
                            coupon.getCouponName(),
                            policy.getCouponPolicyType().getValue(),
                            discountStr,
                            policy.getDiscountLimit(),
                            policy.getCriterionPrice(),
                            coupon.getCreatedDate(),
                            coupon.getUsedAt()
                    );
                })
                .toList();
    }

    @Override
    public List<UsableCouponInfoResponse> getUsableCoupons(String token, List<OrderItemRequest> request) {
        Long memberId = accessTokenParser(token);
        List<Long> bookIds = request.stream()
                .map(OrderItemRequest::bookId)
                .toList();

        //유효한 쿠폰 전부 조회
        List<Coupon> allUsableCoupons = couponRepository.findByMember_MemberIdAndUsedAtIsNullAndExpiredDateGreaterThanEqual(memberId, LocalDate.now());

        //도서들의 카테고리 아이디(부모 카테고리 포함)
        List<Long> allCategoryIds = categoryRepository.findAllAncestorsByBookIds(bookIds);
        Long totalAmount = calculateTotalAmount(request);

        for (Long categoryId : allCategoryIds) {
            log.info("조회된 카테고리: {}", categoryId);
        }
        //OrderContext 초기화
        OrderContext orderContext = new OrderContext(bookIds, allCategoryIds, BigDecimal.valueOf(totalAmount));
        //상품에 적용 가능한 쿠폰들 검증
        return allUsableCoupons.stream()
                .filter(coupon -> {
                    String policyType = coupon.getCouponPolicy().getCouponPolicyType().getValue();
                    CouponValidationStrategy strategy = validationStrategyMap.get(policyType);

                    if (strategy != null) {
                        return strategy.isApplicable(coupon, orderContext);
                    }
                    return false;
                })
                .map(UsableCouponInfoResponse::fromEntity)
                .toList();
        //

    }

    private Long accessTokenParser(String accessToken) {
        return jwtParser.parseToken(accessToken).get("memberId", Long.class);
    }

    @Override
    public Long calculateTotalAmount(List<OrderItemRequest> orderItems) {
        // BigDecimal을 사용하여 금액 계산의 정확성을 확보
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 주문 항목들을 순회하며 금액 계산
        for (OrderItemRequest item : orderItems) {
            // 1. DB에서 해당 도서의 최신 판매 가격(salePrice)을 조회
            Book book = bookRepository.findByBookId(item.bookId()) // 1. Optional<Book>을 반환받음
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서 ID: " + item.bookId()));

            // 2. 판매 가격(salePrice)과 수량(quantity)을 곱함
            BigDecimal itemPrice = book.getSalePrice()
                    .multiply(new BigDecimal(item.quantity()));

            // 3. 총 금액에 합산
            totalAmount = totalAmount.add(itemPrice);
        }

        // 4. 합산된 BigDecimal 금액을 정수형(Long)으로 반환 (원 단위로 가정)
        // 예: 1000.00 -> 1000L
        return totalAmount.longValue();
    }

    /**
     * @param request CouponApplyRequest : couponId, List<OrderItemRequest>
     *                OrderItemRequest : bookId, quantity
     * @return
     */
    @Override
    public Long calculateFinalDiscount(CouponApplyRequest request) {
        // 1. 선택된 쿠폰 및 정책 정보 조회
        Coupon coupon = couponRepository.findById(request.couponId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 ID입니다."));

        CouponPolicy policy = coupon.getCouponPolicy();

        // 2. 주문 컨텍스트 생성에 필요한 데이터 준비
        List<Long> bookIds = request.orderItems().stream()
                .map(OrderItemRequest::bookId)
                .toList();

        Long totalAmount = calculateTotalAmount(request.orderItems()); // 모든 상품의 총 금액

        // 카테고리 정보 조회 (재귀적 CTE 쿼리 사용)
        List<Long> allCategoryIds = categoryRepository.findAllAncestorsByBookIds(bookIds);

        OrderContext orderContext = new OrderContext(bookIds, allCategoryIds, BigDecimal.valueOf(totalAmount));

        // 3. 최소 구매 금액 검증
        if (totalAmount < policy.getCriterionPrice().longValue()) {
            return 0L; // 조건 미달 시 할인 금액 0원
        }

        String policyType = policy.getCouponPolicyType().getValue();

        // 맵에서 전략 꺼내기 (BOOK, CATEGORY, WELCOME)
        CouponCalculationStrategy strategy = calculationStrategyMap.get(policyType);

        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 쿠폰 정책 타입입니다: " + policyType);
        }

        // 전략 실행! (지저분한 if-else가 사라짐)
        BigDecimal targetSubtotal = strategy.calculateTargetSubtotal(policy, request.orderItems(), orderContext);

        // 5. 최종 할인 금액 계산 및 반환
        return calculateDiscountValue(policy, targetSubtotal);
    }


    /**
     * 5. 최종 할인액 계산 (정률/정액 및 최대 한도 처리)
     */
    @Override
    public Long calculateDiscountValue(CouponPolicy policy, BigDecimal targetSubtotal) {
        Long finalDiscount = 0L;

        if (policy.getDiscountRate() != null) {
            // 정률(RATE) 할인
            BigDecimal rate = new BigDecimal(policy.getDiscountRate());
            BigDecimal calculatedDiscount = targetSubtotal.multiply(rate)
                    .divide(new BigDecimal(100), 0, java.math.RoundingMode.DOWN);

            // 최대 할인 금액 제한 적용
            if (policy.getDiscountLimit() != null) {
                finalDiscount = calculatedDiscount.min(policy.getDiscountLimit()).longValue();
            } else {
                finalDiscount = calculatedDiscount.longValue();
            }

        } else if (policy.getDiscountAmount() != null) {
            // 정액(FIXED) 할인
            BigDecimal fixedAmount = policy.getDiscountAmount();

            // 할인액이 대상 상품 금액을 초과하지 않도록 보장
            finalDiscount = fixedAmount.min(targetSubtotal).longValue();
        }

        // 최종 금액은 0원 미만이 될 수 없음
        return Math.max(0L, finalDiscount);
    }

}
