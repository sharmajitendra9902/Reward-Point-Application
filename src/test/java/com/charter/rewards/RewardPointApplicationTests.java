package com.charter.rewards;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RewardPointApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnRewardsSuccessfully() throws Exception {
		// Full stack test against actual seeded H2 DB
		mockMvc.perform(get("/api/v1/rewards")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].customerId").exists()).andExpect(jsonPath("$[0].pointsByMonth").isArray())
				.andExpect(jsonPath("$[0].pointsByMonth[0].month").exists())
				.andExpect(jsonPath("$[0].pointsByMonth[0].year").exists())
				.andExpect(jsonPath("$[0].pointsByMonth[0].points").exists());
	}

	@Test
	void shouldReturn400_WhenDatesAreInverted() throws Exception {
		mockMvc.perform(get("/api/v1/rewards").param("startDate", "2026-03-31").param("endDate", "2026-01-01"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("BAD_REQUEST_001"))
				.andExpect(jsonPath("$.errorMessage").value("Start date cannot fall after end date."));
	}

	@Test
	void shouldReturn400_WhenRangeExceeds3Months() throws Exception {
		mockMvc.perform(get("/api/v1/rewards").param("startDate", "2026-01-01").param("endDate", "2026-05-01"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("BAD_REQUEST_001"))
				.andExpect(jsonPath("$.errorMessage").value("Date range cannot exceed 3 months."));
	}

	@Test
	void shouldReturn400_WhenDateIsMalformed() throws Exception {
		mockMvc.perform(get("/api/v1/rewards").param("startDate", "2026-15-32") // Invalid date
				.param("endDate", "2026-03-01")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value("BAD_REQUEST_003"))
				.andExpect(jsonPath("$.errorMessage").exists());
	}

	@Test
	void shouldReturn404_WhenNoTransactionsFound() throws Exception {
		mockMvc.perform(get("/api/v1/rewards").param("startDate", "2099-01-01") // Year in future with no seeded data
				.param("endDate", "2099-03-01")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorCode").value("NOT_FOUND_001"))
				.andExpect(jsonPath("$.errorMessage").value("No transactions found in the given date range."));
	}
}