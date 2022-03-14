package com.circlework.manager;

import com.google.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DefaultAuthService implements AuthService {
    // TODO: Remove
    private final Map<String, UserSession> authMap = new ConcurrentHashMap<>(); //token, user_id

    @Override
    public boolean validateToken(String token){
        return authMap.containsKey(token);
    }

    @Override
    public Optional<UserSession> getUserFromToken(String token){
        if(authMap.containsKey(token)){
            return Optional.of(authMap.get(token));
        }
        return Optional.empty();
    }

    @Override
    public void addToken(String token, UserSession userSession){
        authMap.put(token, userSession);
    }

    @Override
    public boolean removeToken(String token){
        if(!authMap.containsKey(token)) return false;
        authMap.remove(token);
        return true;
    }
}
