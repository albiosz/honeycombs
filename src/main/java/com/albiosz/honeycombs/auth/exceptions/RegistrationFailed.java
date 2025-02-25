package com.albiosz.honeycombs.auth.exceptions;

public class RegistrationFailed extends RuntimeException {
	public RegistrationFailed() {
		super("Registration failed!");
	}
}
