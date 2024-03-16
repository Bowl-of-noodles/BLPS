package com.javadevjournal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenDTO {
    private String username;
    private String token;

    public TokenDTO(String username, String token){
        this.username = username;
        this.token = token;
    }
}
