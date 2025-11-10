package com.nhnacademy.byeol23backend.bookset.publisher.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "publishers")
@NoArgsConstructor
public class Publisher {
	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "publisher_id")
	private Long publisherId;

	@Column(name = "publisher_name", nullable = false, length = 50)
	@Setter
	private String publisherName;

	public Publisher(String publisherName) {
		this.publisherName = publisherName;
	}
}
