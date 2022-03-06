package com.circlework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthManager{
    private Map<String, Integer> authMap = new HashMap<>(); //token, user_id

    public boolean validateToken(String token){
        return authMap.containsKey(token);
    }

    public Optional<Integer> getUserFromToken(String token){
        if(authMap.containsKey(token)){
            return Optional.of(authMap.get(token));
        }
        return Optional.empty();
    }

    public void addToken(String token, int userId){
        authMap.put(token, userId);
    }

    public boolean removeToken(String token){
        if(!authMap.containsKey(token)) return false;
        authMap.remove(token);
        return true;
    }
}