package com.charter.rewards.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RewardsCalculationUtilTest {

	@ParameterizedTest(name = "Amount {0} should calculate to {1} points")
	@CsvSource({ "120, 90.00", "120.99, 91.98", // Fractional points are fully preserved
			"75, 25.00", "100, 50.00", "50, 0.00", "49.99, 0.00", "-150.00, 0.00", ", 0.00" // tests null input
	})
	void calculatePoints_Parameterized(String amountStr, String expectedPointsStr) {
		BigDecimal amount = amountStr == null ? null : new BigDecimal(amountStr);
		BigDecimal expectedPoints = expectedPointsStr == null ? BigDecimal.ZERO : new BigDecimal(expectedPointsStr);

		BigDecimal actualPoints = RewardsCalculationUtil.calculatePoints(amount);

		// Use compareTo to ignore scale differences (e.g., 90 vs 90.00)
		// compareTo returns 0 if they are mathematically equal
		assertEquals(0, expectedPoints.compareTo(actualPoints),
				"Expected " + expectedPoints + " but got " + actualPoints);
	}
}