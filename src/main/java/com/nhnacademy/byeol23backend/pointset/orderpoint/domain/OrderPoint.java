package com.nhnacademy.byeol23backend.pointset.orderpoint.domain;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_point")
@Getter
@NoArgsConstructor
public class OrderPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_point_id")
    private Long orderPointId;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @JoinColumn(name = "point_history_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PointHistory pointHistory;
}

