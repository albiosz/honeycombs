package com.albiosz.honeycombs.config.exceptions;

import com.albiosz.honeycombs.game.exceptions.GameNotModifiable;
import com.albiosz.honeycombs.game.exceptions.GameNotFound;
import com.albiosz.honeycombs.game.exceptions.UserNotGameHost;
import com.albiosz.honeycombs.game.exceptions.UserNotInGame;
import com.albiosz.honeycombs.user.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value={
			GameNotModifiable.class,
			UserNotInGame.class,
			UserNotGameHost.class
	})
	public @ResponseBody ErrorResponse handleBadRequest(
			RuntimeException ex
	) {
		return new ErrorResponse(
						ex.getMessage(),
						"",
						null
				);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value={
			GameNotFound.class,
			UserNotFound.class
	})
	public @ResponseBody ErrorResponse handleNotFound(
			RuntimeException ex
	) {
		return new ErrorResponse(
						ex.getMessage(),
						"",
						null
				);
	}
}
