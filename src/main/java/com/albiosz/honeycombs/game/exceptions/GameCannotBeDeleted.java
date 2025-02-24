package com.albiosz.honeycombs.game.exceptions;

public class GameCannotBeDeleted extends RuntimeException {
	public GameCannotBeDeleted() {
		super("Game cannot be deleted!");
	}
}
