package com.ttt.server.service;

/**
 * Description 工厂
 * DATA 2023-12-22
 *
 * @Author ttt
 */
public class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return userService;
    }
}
