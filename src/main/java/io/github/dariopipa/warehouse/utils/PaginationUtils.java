package io.github.dariopipa.warehouse.utils;

import org.springframework.data.domain.Page;

import io.github.dariopipa.warehouse.dtos.responses.PaginatedResponse;

public class PaginationUtils {

	private PaginationUtils() {

	}

	public static <T> PaginatedResponse<T> buildPaginatedResponse(
			Page<T> pageData) {

		PaginatedResponse<T> response = new PaginatedResponse<>();

		response.setData(pageData.getContent());
		response.setCurrentPage(pageData.getNumber());
		response.setTotalPages(pageData.getTotalPages());
		response.setTotalItems(pageData.getTotalElements());
		response.setPageSize(pageData.getSize());
		response.setHasNext(pageData.hasNext());
		response.setHasPrevious(pageData.hasPrevious());

		return response;
	}

}
