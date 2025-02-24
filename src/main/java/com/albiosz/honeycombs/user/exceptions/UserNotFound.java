package com.albiosz.honeycombs.user.exceptions;

public class UserNotFound extends RuntimeException {
	public UserNotFound() {
		super("User Not Found!");
	}
}
