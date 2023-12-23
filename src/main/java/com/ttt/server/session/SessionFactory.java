package com.ttt.server.session;

/**
 * Description TODO
 * DATA 2023-12-23
 *
 * @Author ttt
 */
public class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
