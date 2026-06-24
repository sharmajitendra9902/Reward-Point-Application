package com.charter.rewards.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.charter.rewards.model.dto.CustomerRewardResponse;
import com.charter.rewards.service.RewardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "Endpoints for calculating customer reward points")
@Slf4j
public class RewardController {

	private final RewardService rewardService;

	@Operation(summary = "Get reward points for all customers", description = "Calculates reward points based on transaction history within a specified date range. If no dates are provided, defaults to the last 3 months.")
	@GetMapping
	public ResponseEntity<List<CustomerRewardResponse>> getRewards(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		log.info("Received request to calculate rewards from {} to {}", startDate, endDate);
		List<CustomerRewardResponse> responses = rewardService.calculateRewards(startDate, endDate);
		return ResponseEntity.ok(responses);
	}
}