package com.ryh.shortlink.allinone.common.utils;

import com.ryh.shortlink.allinone.dao.entity.UserDO;
import jakarta.servlet.http.HttpSession;

/**
 * Session工具类
 */
public class SessionUtils {

    public static final String SESSION_USER = "loginUser";
    public static final String SESSION_TOKEN = "loginToken";

    public static void setUser(HttpSession session, UserDO user) {
        session.setAttribute(SESSION_USER, user);
    }

    public static void setUser(HttpSession session, String username, String token) {
        UserDO user = new UserDO();
        user.setUsername(username);
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_TOKEN, token);
    }

    public static UserDO getUser(HttpSession session) {
        return (UserDO) session.getAttribute(SESSION_USER);
    }

    public static Long getUserId(HttpSession session) {
        UserDO user = getUser(session);
        return user != null ? user.getId() : null;
    }

    public static String getUsername(HttpSession session) {
        UserDO user = getUser(session);
        return user != null ? user.getUsername() : null;
    }

    public static String getToken(HttpSession session) {
        return (String) session.getAttribute(SESSION_TOKEN);
    }

    public static boolean isAdmin(HttpSession session) {
        UserDO user = getUser(session);
        return user != null && "admin".equals(user.getRole());
    }

    public static void removeUser(HttpSession session) {
        session.removeAttribute(SESSION_USER);
        session.removeAttribute(SESSION_TOKEN);
    }
}
