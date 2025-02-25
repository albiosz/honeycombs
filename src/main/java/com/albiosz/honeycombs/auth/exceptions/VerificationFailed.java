package com.albiosz.honeycombs.auth.exceptions;

public class VerificationFailed extends RuntimeException {
	public VerificationFailed() {
		super("Verification failed!");
	}
}
