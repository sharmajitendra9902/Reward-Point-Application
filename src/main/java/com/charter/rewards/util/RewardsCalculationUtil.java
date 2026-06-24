package com.charter.rewards.util;

import java.math.BigDecimal;

public class RewardsCalculationUtil {

	private static final BigDecimal HUNDRED = new BigDecimal("100");
	private static final BigDecimal FIFTY = new BigDecimal("50");
	private static final BigDecimal TWO = new BigDecimal("2");

	private RewardsCalculationUtil() {
	}

	public static BigDecimal calculatePoints(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		if (amount.compareTo(HUNDRED) > 0) {
			return amount.subtract(HUNDRED).multiply(TWO).add(FIFTY);
		} else if (amount.compareTo(FIFTY) > 0) {
			return amount.subtract(FIFTY);
		}

		return BigDecimal.ZERO;
	}
}