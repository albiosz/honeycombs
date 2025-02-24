package com.albiosz.honeycombs.game.exceptions;

public class UserNotInGame extends RuntimeException {
	public UserNotInGame() {
		super("Currently logged in user is not in the game!");
	}
}
