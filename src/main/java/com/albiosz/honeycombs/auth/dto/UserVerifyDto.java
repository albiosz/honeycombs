package com.albiosz.honeycombs.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVerifyDto {
	private String username;
	private String verificationCode;
}
