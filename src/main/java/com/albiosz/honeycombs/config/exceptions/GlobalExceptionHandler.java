package com.albiosz.honeycombs.config.exceptions;

import com.albiosz.honeycombs.game.exceptions.GameCannotBeDeleted;
import com.albiosz.honeycombs.game.exceptions.UserNotGameHost;
import com.albiosz.honeycombs.game.exceptions.UserNotInGame;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value={
			GameCannotBeDeleted.class,
			UserNotInGame.class,
			UserNotGameHost.class
	})
	public @ResponseBody ErrorResponse handleGameCannotBeDeleted(
			RuntimeException ex
	) {
		return new ErrorResponse(
						ex.getMessage(),
						"",
						null
				);
	}
}
