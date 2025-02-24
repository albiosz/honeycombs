package com.albiosz.honeycombs.game.exceptions;

public class GameNotModifiable extends RuntimeException {
	public GameNotModifiable() {
		super("Game cannot be modified!");
	}
}
