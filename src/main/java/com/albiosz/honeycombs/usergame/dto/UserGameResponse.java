package com.albiosz.honeycombs.usergame.dto;

import com.albiosz.honeycombs.game.dto.GameResponse;
import com.albiosz.honeycombs.turn.dto.TurnResponse;
import com.albiosz.honeycombs.user.dto.UserResponse;
import com.albiosz.honeycombs.usergame.State;
import com.albiosz.honeycombs.usergame.UserGameId;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserGameResponse {

    @JsonProperty(required = true)
    private UserResponse user;

    @JsonProperty(required = false)
    @JsonBackReference
    private GameResponse game;

    @JsonProperty(required = true)
    private Instant createdAt;

    @JsonProperty(required = true)
    private int playerNo;

    @JsonProperty(required = true)
    private boolean isUserHost;

    @JsonProperty(required = true)
    private State state;

    @JsonProperty(required = true)
    @JsonManagedReference
    private List<TurnResponse> turns;
}
