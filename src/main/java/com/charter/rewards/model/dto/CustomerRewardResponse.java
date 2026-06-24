package com.charter.rewards.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRewardResponse {
	private String customerId;
	private String customerName;
	private String evaluationPeriod;
	private long totalTransactionsProcessed;
	private List<MonthlyRewardSummary> pointsByMonth;
	private BigDecimal totalRewardPoints;
}