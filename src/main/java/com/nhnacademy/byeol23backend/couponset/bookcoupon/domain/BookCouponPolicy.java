package com.nhnacademy.byeol23backend.couponset.bookcoupon.domain;

import com.nhnacademy.byeol23backend.bookset.book.domain.Book;
import com.nhnacademy.byeol23backend.couponset.couponpolicy.domain.CouponPolicy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "book_coupon_policy")
public class BookCouponPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_coupon_id")
	private Long bookCouponId;

	@Setter
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coupon_policy_id", nullable = false)
	private CouponPolicy couponPolicy;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	private BookCouponPolicy(CouponPolicy couponPolicy, Book book) {
		this.couponPolicy = couponPolicy;
		this.book = book;
	}

	public static BookCouponPolicy createFromDto(CouponPolicy couponPolicy, Book book){
		return new BookCouponPolicy(couponPolicy, book);
	}

}
