package com.charter.rewards.exception;

public class TransactionNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1664535111739375415L;

	public TransactionNotFoundException(String message) {
		super(message);
	}
}