package com.albiosz.honeycombs.auth.exceptions;

public class LoginNotPossible extends RuntimeException {
	public LoginNotPossible() {
		super("Logging in is currently not possible!");
	}
}
