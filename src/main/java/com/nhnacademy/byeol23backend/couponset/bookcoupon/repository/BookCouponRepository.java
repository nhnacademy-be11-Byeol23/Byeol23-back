package com.nhnacademy.byeol23backend.couponset.bookcoupon.repository;

import com.nhnacademy.byeol23backend.couponset.bookcoupon.domain.BookCouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCouponRepository extends JpaRepository<BookCouponPolicy, Long> {
    List<Long> findBookIdsByCouponPolicyId(Long couponPolicyId);
}
