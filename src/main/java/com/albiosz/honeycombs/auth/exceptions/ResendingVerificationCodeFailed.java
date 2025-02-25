package com.albiosz.honeycombs.auth.exceptions;

public class ResendingVerificationCodeFailed extends RuntimeException {
	public ResendingVerificationCodeFailed() {
		super("Resending verification code failed!");
	}
}
