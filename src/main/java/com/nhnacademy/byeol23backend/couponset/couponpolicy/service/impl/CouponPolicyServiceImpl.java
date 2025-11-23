package com.nhnacademy.byeol23backend.couponset.couponpolicy.service.impl;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.bookset.book.repository.BookRepository;
import com.nhnacademy.byeol23backend.bookset.category.domain.Category;
import com.nhnacademy.byeol23backend.bookset.category.repository.CategoryRepository;
import com.nhnacademy.byeol23backend.couponset.bookcoupon.domain.BookCouponPolicy;
import com.nhnacademy.byeol23backend.couponset.bookcoupon.repository.BookCouponRepository;
import com.nhnacademy.byeol23backend.couponset.categorycoupon.domain.CategoryCouponPolicy;
import com.nhnacademy.byeol23backend.couponset.categorycoupon.repository.CategoryCouponPolicyRepository;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.dto.CouponPolicyInfoResponse;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.repository.CouponPolicyRepository;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.service.CouponPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CouponPolicyServiceImpl implements CouponPolicyService {
    private final CouponPolicyRepository couponPolicyRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponPolicyRepository categoryCouponPolicyRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    @Override
    @Transactional
    public void createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest) {
        //쿠폰정책 생성
        CouponPolicy couponPolicy = new CouponPolicy(couponPolicyCreateRequest.policyName(),
                couponPolicyCreateRequest.criterionPrice(),
                couponPolicyCreateRequest.discountRate(),
                couponPolicyCreateRequest.discountLimit(),
                couponPolicyCreateRequest.discountAmount(),
                couponPolicyCreateRequest.couponPolicyType());
        CouponPolicy savedPolicy = couponPolicyRepository.save(couponPolicy);


        //도서 or 카테고리 쿠폰정책 생성
        if(couponPolicyCreateRequest.couponPolicyType().equals("BOOK")){
            Book book = bookRepository.findById(couponPolicyCreateRequest.bookId()).orElseThrow();
            bookCouponRepository.save(BookCouponPolicy.createFromDto(savedPolicy, book));
        }else{
            Category category = categoryRepository.findById(couponPolicyCreateRequest.categoryIds()).orElseThrow();
            categoryCouponPolicyRepository.save(CategoryCouponPolicy.createFromDto(savedPolicy, category));
        }

    }

    @Override
    public Page<CouponPolicyInfoResponse> getCouponPolicies(Pageable pageable) {
        Page<CouponPolicy> couponPolicies = couponPolicyRepository.findAll(pageable);

        return couponPolicies.map(couponPolicy -> new CouponPolicyInfoResponse(
                couponPolicy.getCouponPolicyId(),
                couponPolicy.getCouponPolicyName(),
                couponPolicy.getCriterionPrice(),
                couponPolicy.getDiscountRate(),
                couponPolicy.getDiscountLimit(),
                couponPolicy.getDiscountAmount(),
                couponPolicy.getCouponPolicyType())
        );
    }
}
