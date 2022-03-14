package com.circlework.manager;

import com.google.inject.AbstractModule;

public class AppInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(AuthService.class).to(DefaultAuthService.class);
        bind(CircleService.class).to(DefaultCircleService.class);
        bind(UserService.class).to(SQLUserService.class);
    }
}
