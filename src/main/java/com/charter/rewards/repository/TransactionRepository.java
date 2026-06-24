package com.charter.rewards.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.charter.rewards.model.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	// JOIN FETCH avoids the N+1 problem with lazy-loaded Customer mapping
	@Query("SELECT t FROM Transaction t JOIN FETCH t.customer WHERE t.date BETWEEN :startDate AND :endDate")
	List<Transaction> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}