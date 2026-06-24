package com.charter.rewards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.charter.rewards.model.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
	// Standard CRUD operations inherited automatically
}