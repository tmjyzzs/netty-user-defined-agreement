package com.ttt.server.service;

/**
 * Description TODO
 * DATA 2023-12-22
 *
 * @Author ttt
 */
public interface UserService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 true, 否则返回 false
     */
    boolean login(String username, String password);
}
