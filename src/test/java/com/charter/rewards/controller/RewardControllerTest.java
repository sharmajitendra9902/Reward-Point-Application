package com.charter.rewards.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.charter.rewards.exception.DateValidationException;
import com.charter.rewards.exception.GlobalExceptionHandler;
import com.charter.rewards.model.dto.CustomerRewardResponse;
import com.charter.rewards.service.RewardService;

@WebMvcTest(RewardController.class)
@Import(GlobalExceptionHandler.class)
class RewardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RewardService rewardService;

	@Test
	void getRewards_ValidRequest_Returns200OKAndJsonArray() throws Exception {
		CustomerRewardResponse mockResponse = CustomerRewardResponse.builder().customerId("CUST001")
				.customerName("John Doe").evaluationPeriod("2026-01-01 to 2026-03-31").totalTransactionsProcessed(2)
				// FIXED: Passed BigDecimal instead of int
				.pointsByMonth(Collections.singletonList(new com.charter.rewards.model.dto.MonthlyRewardSummary(
						"January", "2026", new BigDecimal("90"))))
				// FIXED: Passed BigDecimal instead of int
				.totalRewardPoints(new BigDecimal("90")).build();

		List<CustomerRewardResponse> mockList = Arrays.asList(mockResponse);

		LocalDate expectedStart = LocalDate.of(2026, 1, 1);
		LocalDate expectedEnd = LocalDate.of(2026, 3, 31);

		when(rewardService.calculateRewards(eq(expectedStart), eq(expectedEnd))).thenReturn(mockList);

		mockMvc.perform(get("/api/v1/rewards").param("startDate", "2026-01-01").param("endDate", "2026-03-31"))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].customerId").value("CUST001"))
				.andExpect(jsonPath("$[0].customerName").value("John Doe"))
				.andExpect(jsonPath("$[0].totalRewardPoints").value(90));

		// Verifies the Controller properly parsed the parameters and forwarded them
		verify(rewardService).calculateRewards(expectedStart, expectedEnd);
	}

	@Test
	void getRewards_WithoutDates_Returns200OK() throws Exception {
		when(rewardService.calculateRewards(null, null)).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/v1/rewards")).andExpect(status().isOk());

		verify(rewardService).calculateRewards(null, null);
	}

	@Test
	void getRewards_ServiceThrowsDateValidationException_Returns400BadRequest() throws Exception {
		LocalDate expectedStart = LocalDate.of(2026, 1, 1);
		LocalDate expectedEnd = LocalDate.of(2026, 6, 1);

		when(rewardService.calculateRewards(eq(expectedStart), eq(expectedEnd)))
				.thenThrow(new DateValidationException("Date range cannot exceed 3 months."));

		mockMvc.perform(get("/api/v1/rewards").param("startDate", "2026-01-01").param("endDate", "2026-06-01"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("BAD_REQUEST_001"))
				.andExpect(jsonPath("$.errorMessage").value("Date range cannot exceed 3 months."));

		verify(rewardService).calculateRewards(expectedStart, expectedEnd);
	}
}