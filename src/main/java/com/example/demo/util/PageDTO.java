package com.example.demo.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A generic DTO (Data Transfer Object) class representing a paginated page of data.
 *
 * @param <T> The type of the content in the page.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {

	private List<T> content;
	private Long totalElements;
	private Long totalPages;
	private Long currentPage;

	/**
	 * Constructs a PageDTO object from a Spring Data Page object.
	 *
	 * @param page The Spring Data Page object.
	 */
	public PageDTO(Page<T> page) {
		content = page.getContent();
		currentPage = (long) page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = (long) page.getTotalPages();

	}
}
