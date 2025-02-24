package com.albiosz.honeycombs.game.exceptions;

public class UserNotGameHost extends RuntimeException {
	public UserNotGameHost() {
		super("Currently logged in user is not host of the game!");
	}
}
