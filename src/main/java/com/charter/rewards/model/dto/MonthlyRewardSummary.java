package com.charter.rewards.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRewardSummary {
	private String month;
	private String year;
	private BigDecimal points;
}