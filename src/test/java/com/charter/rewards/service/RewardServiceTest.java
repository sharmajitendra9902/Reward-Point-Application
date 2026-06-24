package com.charter.rewards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.charter.rewards.exception.TransactionNotFoundException;
import com.charter.rewards.model.dto.CustomerRewardResponse;
import com.charter.rewards.model.entity.Customer;
import com.charter.rewards.model.entity.Transaction;
import com.charter.rewards.repository.TransactionRepository;
import com.charter.rewards.validation.RewardValidator;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private RewardValidator rewardValidator;

	@InjectMocks
	private RewardService rewardService;

	private Customer testCustomer1;
	private Customer testCustomer2;
	private LocalDate startDate;
	private LocalDate endDate;
	private Transaction t1, t2, t3;

	@BeforeEach
	void setUp() {
		testCustomer1 = new Customer("C1", "John Doe");
		testCustomer2 = new Customer("C2", "Jane Smith");
		startDate = LocalDate.of(2026, 1, 1);
		endDate = LocalDate.of(2026, 3, 31);

		// Customer 1: Transactions in Jan and Feb
		t1 = new Transaction(1L, testCustomer1, new BigDecimal("120.00"), LocalDate.of(2026, 1, 10)); // 90.00 pts
		t2 = new Transaction(2L, testCustomer1, new BigDecimal("75.00"), LocalDate.of(2026, 2, 15)); // 25.00 pts

		// Customer 2: Transactions in March
		t3 = new Transaction(3L, testCustomer2, new BigDecimal("200.00"), LocalDate.of(2026, 3, 5)); // 250.00 pts
	}

	@Test
	void calculateRewards_CalculatesCorrectPointTotalsForMultipleCustomers() {
		when(rewardValidator.validateAndGetDateRange(startDate, endDate))
				.thenReturn(new RewardValidator.DateRange(startDate, endDate));
		when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList(t1, t2, t3));

		List<CustomerRewardResponse> responses = rewardService.calculateRewards(startDate, endDate);

		assertEquals(2, responses.size());

		// Validate Customer 1 Total: 90 + 25 = 115
		CustomerRewardResponse c1Response = responses.stream().filter(r -> r.getCustomerId().equals("C1")).findFirst()
				.get();
		assertEquals(0, new BigDecimal("115").compareTo(c1Response.getTotalRewardPoints()));

		// Validate Customer 2 Total: 250
		CustomerRewardResponse c2Response = responses.stream().filter(r -> r.getCustomerId().equals("C2")).findFirst()
				.get();
		assertEquals(0, new BigDecimal("250").compareTo(c2Response.getTotalRewardPoints()));
	}

	@Test
	void calculateRewards_SplitsPointsCorrectlyAcrossMultipleMonths() {
		when(rewardValidator.validateAndGetDateRange(startDate, endDate))
				.thenReturn(new RewardValidator.DateRange(startDate, endDate));
		when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList(t1, t2)); // Only C1
																												// transactions

		List<CustomerRewardResponse> responses = rewardService.calculateRewards(startDate, endDate);
		CustomerRewardResponse c1Response = responses.get(0);

		assertEquals(2, c1Response.getPointsByMonth().size());

		// Check Jan Points
		assertEquals(0, new BigDecimal("90").compareTo(c1Response.getPointsByMonth().get(0).getPoints()));

		// Check Feb Points
		assertEquals(0, new BigDecimal("25").compareTo(c1Response.getPointsByMonth().get(1).getPoints()));
	}

	@Test
	void calculateRewards_MaintainsChronologicalSortOrder() {
		when(rewardValidator.validateAndGetDateRange(startDate, endDate))
				.thenReturn(new RewardValidator.DateRange(startDate, endDate));
		when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList(t1, t2));

		List<CustomerRewardResponse> responses = rewardService.calculateRewards(startDate, endDate);
		CustomerRewardResponse c1Response = responses.get(0);

		assertEquals("January", c1Response.getPointsByMonth().get(0).getMonth());
		assertEquals("February", c1Response.getPointsByMonth().get(1).getMonth());
	}

	@Test
	void calculateRewards_ExactBoundaryAmount_ReturnsZeroPoints() {
		when(rewardValidator.validateAndGetDateRange(startDate, endDate))
				.thenReturn(new RewardValidator.DateRange(startDate, endDate));

		// Exactly $50.00 -> should return 0 points
		Transaction boundaryTx = new Transaction(1L, testCustomer1, new BigDecimal("50.00"), LocalDate.of(2026, 1, 10));

		when(transactionRepository.findByDateBetween(startDate, endDate))
				.thenReturn(Collections.singletonList(boundaryTx));

		List<CustomerRewardResponse> responses = rewardService.calculateRewards(startDate, endDate);
		CustomerRewardResponse response = responses.get(0);

		assertEquals(0, BigDecimal.ZERO.compareTo(response.getTotalRewardPoints()));
		assertEquals(0, BigDecimal.ZERO.compareTo(response.getPointsByMonth().get(0).getPoints()));
	}

	@Test
	void calculateRewards_NoTransactionsFound_ThrowsTransactionNotFoundException() {
		when(rewardValidator.validateAndGetDateRange(startDate, endDate))
				.thenReturn(new RewardValidator.DateRange(startDate, endDate));
		when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Collections.emptyList());

		assertThrows(TransactionNotFoundException.class, () -> {
			rewardService.calculateRewards(startDate, endDate);
		});
	}
}