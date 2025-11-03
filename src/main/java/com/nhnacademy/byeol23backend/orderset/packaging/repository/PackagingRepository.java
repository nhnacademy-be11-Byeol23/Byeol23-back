package com.nhnacademy.byeol23backend.orderset.packaging.repository;

import java.util.List;

import com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.Packaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PackagingRepository extends JpaRepository<Packaging, Long> {

	// @Query(
	// 	"SELECT new com.nhnacademy.byeol23backend.image.dto.ImageUrlProjection(p.packagingId, p.packagingImg) " +
	// 		"FROM Packaging p " +
	// 		"WHERE p.packagingId = :packagingId"
	// )
	// List<ImageUrlProjection> getImageUrlsByPackagingId(Long packagingId);
}
