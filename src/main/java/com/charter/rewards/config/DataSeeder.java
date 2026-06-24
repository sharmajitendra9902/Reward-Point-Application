
package com.charter.rewards.config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.charter.rewards.model.entity.Customer;
import com.charter.rewards.model.entity.Transaction;
import com.charter.rewards.repository.CustomerRepository;
import com.charter.rewards.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

	private final CustomerRepository customerRepository;
	private final TransactionRepository transactionRepository;

	@Override
	public void run(String... args) {
		if (customerRepository.count() == 0) {
			log.info("Seeding database with explicit test scenarios...");

			// 1. Create specific test customers
			Customer edgeCaseCustomer = customerRepository.save(new Customer("C1", "Customer 1"));
			Customer timelineCustomer = customerRepository.save(new Customer("C2", "Customer 2"));

			log.info("Created deterministic test customers.");

			saveTransaction(edgeCaseCustomer, "120.99", LocalDate.of(2026, 6, 15));

			saveTransaction(edgeCaseCustomer, "50.50", LocalDate.of(2026, 6, 16));

			saveTransaction(edgeCaseCustomer, "100.75", LocalDate.of(2026, 6, 17));

			saveTransaction(edgeCaseCustomer, "50.00", LocalDate.of(2026, 5, 15));

			saveTransaction(edgeCaseCustomer, "100.00", LocalDate.of(2026, 4, 15));

			saveTransaction(edgeCaseCustomer, "-25.50", LocalDate.of(2026, 3, 15));

			saveTransaction(edgeCaseCustomer, "0.00", LocalDate.of(2026, 2, 15));

			// Using explicit dates so you can test specific date range queries

			// Previous Year (Cross-year check) -> Expected: 250 points in Dec 2025
			saveTransaction(timelineCustomer, "200.00", LocalDate.of(2025, 12, 15));

			// Month 1 -> Expected: 150 points in Jan 2026
			saveTransaction(timelineCustomer, "150.00", LocalDate.of(2026, 1, 10));

			// Month 2 -> Expected: 25 points in Feb 2026
			saveTransaction(timelineCustomer, "75.00", LocalDate.of(2026, 2, 20));

			// Month 3 -> Expected: 70 points in Mar 2026
			saveTransaction(timelineCustomer, "110.00", LocalDate.of(2026, 3, 5));

			Random random = new Random(12345L);
			List<Customer> randomCustomers = new ArrayList<>();
			for (int i = 3; i <= 5; i++) {
				randomCustomers.add(customerRepository.save(new Customer("C" + i, "Customer " + i)));
			}

			int randomTxCount = 0;
			for (Customer customer : randomCustomers) {
				int numTransactions = 3 + random.nextInt(3);
				for (int j = 0; j < numTransactions; j++) {
					double amount = 10 + (150 - 10) * random.nextDouble();
					LocalDate date = LocalDate.now().minusDays(random.nextInt(90));
					saveTransaction(customer, String.valueOf(amount), date);
					randomTxCount++;
				}
			}

			log.info("Created 11 explicit edge-case transactions and {} random volume transactions.", randomTxCount);
		}
	}

	private void saveTransaction(Customer customer, String amountString, LocalDate date) {
		Transaction t = new Transaction();
		t.setCustomer(customer);
		t.setAmount(new BigDecimal(amountString).setScale(2, RoundingMode.HALF_UP));
		t.setDate(date);
		transactionRepository.save(t);
	}
}