package com.nhnacademy.byeol23backend.pointset.pointhistories.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.pointset.pointhistories.domain.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
