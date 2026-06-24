package com.charter.rewards.validation;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.charter.rewards.exception.DateValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;

@Component
public class RewardValidator {

	public DateRange validateAndGetDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate == null && endDate == null) {
			LocalDate end = LocalDate.now();
			LocalDate start = end.minusMonths(3);
			return new DateRange(start, end);
		}

		if (startDate == null || endDate == null) {
			throw new DateValidationException("Both start date and end date must be provided, or both must be empty.");
		}

		if (startDate.isAfter(endDate)) {
			throw new DateValidationException("Start date cannot fall after end date.");
		}

		if (startDate.plusMonths(3).isBefore(endDate)) {
			throw new DateValidationException("Date range cannot exceed 3 months.");
		}

		return new DateRange(startDate, endDate);
	}

	@Data
	@AllArgsConstructor
	public static class DateRange {
		private LocalDate startDate;
		private LocalDate endDate;
	}
}