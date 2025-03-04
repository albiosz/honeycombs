package com.albiosz.honeycombs.turn.dto;

import com.albiosz.honeycombs.usergame.dto.UserGameResponse;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TurnResponse {

    @JsonProperty(required = true)
    private long id;

    @JsonProperty(required = true)
    @JsonBackReference
    private UserGameResponse userGame;

    @JsonProperty(required = true)
    private Instant createdAt;

    @JsonProperty(required = true)
    private int points;
}
