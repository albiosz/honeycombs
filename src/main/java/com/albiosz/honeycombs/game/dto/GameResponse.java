package com.albiosz.honeycombs.game.dto;

import com.albiosz.honeycombs.game.Game;
import com.albiosz.honeycombs.game.State;
import com.albiosz.honeycombs.usergame.dto.UserGameResponse;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GameResponse {

    @JsonProperty(required = true)
    private long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private Instant createdAt;

    @JsonProperty(required = true)
    private State state;

    @JsonProperty(required = true)
    @JsonManagedReference
    private Map<UUID, UserGameResponse> userGames;

    public static GameResponse fromGame(Game game, ModelMapper modelMapper) {
        return modelMapper.map(game, GameResponse.class);
    }
}
