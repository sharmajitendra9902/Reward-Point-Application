package com.charter.rewards.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.charter.rewards.exception.DateValidationException;

class RewardValidatorTest {

	private RewardValidator rewardValidator;

	@BeforeEach
	void setUp() {
		rewardValidator = new RewardValidator();
	}

	@Test
	void validateAndGetDateRange_BothDatesNull_ReturnsDefault3MonthRange() {
		RewardValidator.DateRange range = rewardValidator.validateAndGetDateRange(null, null);

		assertNotNull(range);
		assertEquals(LocalDate.now(), range.getEndDate());
		assertEquals(LocalDate.now().minusMonths(3), range.getStartDate());
	}

	@ParameterizedTest(name = "Start: {0}, End: {1} throws DateValidationException")
	@MethodSource("provideNullDateCombinations")
	void validateAndGetDateRange_MissingDates_ThrowsDateValidationException(LocalDate start, LocalDate end) {
		Exception exception = assertThrows(DateValidationException.class, () -> {
			rewardValidator.validateAndGetDateRange(start, end);
		});

		assertEquals("Both start date and end date must be provided, or both must be empty.", exception.getMessage());
	}

	private static Stream<Arguments> provideNullDateCombinations() {
		return Stream.of(Arguments.of(null, LocalDate.now()), Arguments.of(LocalDate.now().minusMonths(1), null));
	}

	@Test
	void validateAndGetDateRange_StartDateAfterEndDate_ThrowsDateValidationException() {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.minusDays(1);

		Exception exception = assertThrows(DateValidationException.class, () -> {
			rewardValidator.validateAndGetDateRange(startDate, endDate);
		});

		assertEquals("Start date cannot fall after end date.", exception.getMessage());
	}

	@Test
	void validateAndGetDateRange_RangeExceeds3Months_ThrowsDateValidationException() {
		LocalDate startDate = LocalDate.now().minusMonths(4);
		LocalDate endDate = LocalDate.now();

		Exception exception = assertThrows(DateValidationException.class, () -> {
			rewardValidator.validateAndGetDateRange(startDate, endDate);
		});

		assertEquals("Date range cannot exceed 3 months.", exception.getMessage());
	}

	@Test
	void validateAndGetDateRange_ValidDates_ReturnsSameDates() {
		LocalDate startDate = LocalDate.now().minusMonths(2);
		LocalDate endDate = LocalDate.now();

		RewardValidator.DateRange range = rewardValidator.validateAndGetDateRange(startDate, endDate);

		assertEquals(startDate, range.getStartDate());
		assertEquals(endDate, range.getEndDate());
	}
}