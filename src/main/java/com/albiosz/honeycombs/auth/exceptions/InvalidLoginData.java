package com.albiosz.honeycombs.auth.exceptions;

public class InvalidLoginData extends RuntimeException {
	public InvalidLoginData() {
		super("Given login data is not valid!");
	}
}
