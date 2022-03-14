package com.circlework.manager;

import com.google.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DefaultAuthService implements AuthService {
    // TODO: Remove
    private final Map<String, Integer> authMap = new ConcurrentHashMap<>(Map.of("88006c51-4396-42f8-bd78-7a23e1e06c74", 2)); //token, user_id

    @Override
    public boolean validateToken(String token){
        return authMap.containsKey(token);
    }

    @Override
    public Optional<Integer> getUserFromToken(String token){
        if(authMap.containsKey(token)){
            return Optional.of(authMap.get(token));
        }
        return Optional.empty();
    }

    @Override
    public void addToken(String token, int userId){
        authMap.put(token, userId);
    }

    @Override
    public boolean removeToken(String token){
        if(!authMap.containsKey(token)) return false;
        authMap.remove(token);
        return true;
    }
}
