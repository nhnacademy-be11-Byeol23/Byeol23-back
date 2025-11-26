package com.nhnacademy.byeol23backend.cartset.cart.domain;

import com.nhnacademy.byeol23backend.cartset.cartbook.domain.CartBook;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long cartId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	@Setter
	private Member member;

	@OneToMany(mappedBy = "cart")
	private List<CartBook> cartBooks = new ArrayList<>();

    private Cart(Member member) {
        this.member = member;
    }

    public static Cart create(Member member) {
        return new Cart(member);
    }
}
