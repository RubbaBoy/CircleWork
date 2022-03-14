package com.circlework.manager;

import com.circlework.Objects;

import java.util.concurrent.CompletableFuture;

public interface UserService {

    // TODO: Separate request to get circle_id from user that's cached?
    CompletableFuture<Objects.User> getUser(int userId);

}
