package com.albiosz.honeycombs.config.exceptions;

import java.io.Serializable;
import java.util.Map;

public record ErrorResponse (
		String message,
		String details,
		Map<String, String> errors) implements Serializable {

}
