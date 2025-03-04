package com.albiosz.honeycombs.user.dto;

import com.albiosz.honeycombs.user.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.UUID;

@Setter
@Getter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserResponse {
    @JsonProperty(required = true)
    private UUID id;

    @JsonProperty(required = true)
    private String username;

    @JsonProperty(required = true)
    private String nickname;

    @JsonProperty(value = "isEnabled", required = true)
    private boolean isEnabled;

    public static UserResponse fromUser(User user, ModelMapper modelMapper) {
        return modelMapper.map(user, UserResponse.class);
    }
}
