package com.charter.rewards.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.charter.rewards.exception.TransactionNotFoundException;
import com.charter.rewards.model.dto.CustomerRewardResponse;
import com.charter.rewards.model.dto.MonthlyRewardSummary;
import com.charter.rewards.model.entity.Customer;
import com.charter.rewards.model.entity.Transaction;
import com.charter.rewards.repository.TransactionRepository;
import com.charter.rewards.util.RewardsCalculationUtil;
import com.charter.rewards.validation.RewardValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RewardService {

	private final TransactionRepository transactionRepository;
	private final RewardValidator rewardValidator;

	@Transactional(readOnly = true)
	public List<CustomerRewardResponse> calculateRewards(LocalDate startDate, LocalDate endDate) {
		RewardValidator.DateRange dateRange = rewardValidator.validateAndGetDateRange(startDate, endDate);

		log.info("Fetching transactions between {} and {}", dateRange.getStartDate(), dateRange.getEndDate());

		List<Transaction> transactions = transactionRepository.findByDateBetween(dateRange.getStartDate(),
				dateRange.getEndDate());

		if (transactions.isEmpty()) {
			throw new TransactionNotFoundException("No transactions found in the given date range.");
		}

		Map<Customer, List<Transaction>> transactionsByCustomer = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getCustomer));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String evaluationPeriod = String.format("%s to %s", dateRange.getStartDate().format(formatter),
				dateRange.getEndDate().format(formatter));

		return transactionsByCustomer.entrySet().stream()
				.map(entry -> buildCustomerResponse(entry.getKey(), entry.getValue(), evaluationPeriod))
				.sorted(Comparator.comparing(CustomerRewardResponse::getCustomerId)).collect(Collectors.toList());

	}

	private CustomerRewardResponse buildCustomerResponse(Customer customer, List<Transaction> transactions,
			String evaluationPeriod) {

		Map<YearMonth, BigDecimal> pointsByYearMonth = transactions.stream()
				.collect(Collectors.groupingBy(t -> YearMonth.from(t.getDate()), Collectors.reducing(BigDecimal.ZERO,
						t -> RewardsCalculationUtil.calculatePoints(t.getAmount()), BigDecimal::add)));

		List<MonthlyRewardSummary> pointsByMonth = pointsByYearMonth.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(entry -> new MonthlyRewardSummary(
						entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
						String.valueOf(entry.getKey().getYear()), entry.getValue()))
				.collect(Collectors.toList());

		BigDecimal totalPoints = pointsByMonth.stream().map(MonthlyRewardSummary::getPoints).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		return CustomerRewardResponse.builder().customerId(customer.getId()).customerName(customer.getName())
				.evaluationPeriod(evaluationPeriod).totalTransactionsProcessed(transactions.size())
				.pointsByMonth(pointsByMonth).totalRewardPoints(totalPoints).build();
	}
}