package com.kbb.servlet.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

@WebServlet(urlPatterns = {"/login", "/logout", "/session"})
public class LoginServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/hub_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin1";

    private void setCorsHeaders(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        if (origin != null && !origin.isEmpty()) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
        }
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Origin, Authorization");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(req, resp);
        resp.setContentType("application/json;charset=UTF-8");

        String val = readCookie(req, "user_session");
        if (val != null && !val.isEmpty()) {
            String[] parts = val.split(":", 3);
            if (parts.length >= 2) {
                resp.setStatus(200);
                mapper.writeValue(resp.getWriter(), Map.of(
                        "loggedIn", true,
                        "userId",   Long.parseLong(parts[0]),
                        "username", parts[1]
                ));
                return;
            }
        }
        resp.setStatus(200);
        mapper.writeValue(resp.getWriter(), Map.of("loggedIn", false, "username", "Гість"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(req, resp);
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getServletPath();

        if ("/login".equals(path)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                resp.setStatus(400);
                mapper.writeValue(resp.getWriter(), Map.of("error", "Відсутні дані для входу"));
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, username.trim());
                        pstmt.setString(2, password.trim());
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                long   userId      = rs.getLong("id");
                                String dbUsername  = rs.getString("username"); // беремо з БД
                                String cookieVal   = userId + ":" + dbUsername + ":" + UUID.randomUUID();

                                Cookie cookie = new Cookie("user_session", cookieVal);
                                cookie.setMaxAge(60 * 60 * 24);
                                cookie.setPath("/");
                                cookie.setHttpOnly(false);
                                resp.addCookie(cookie);

                                resp.setStatus(200);
                                mapper.writeValue(resp.getWriter(), Map.of(
                                        "ok",       true,
                                        "loggedIn", true,
                                        "userId",   userId,
                                        "username", dbUsername
                                ));
                            } else {
                                resp.setStatus(401);
                                mapper.writeValue(resp.getWriter(),
                                        Map.of("ok", false, "error", "Невірний логін або пароль"));
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                resp.setStatus(500);
                mapper.writeValue(resp.getWriter(), Map.of("error", "MySQL драйвер не знайдено"));
            } catch (SQLException e) {
                resp.setStatus(500);
                mapper.writeValue(resp.getWriter(), Map.of("error", "Помилка БД: " + e.getMessage()));
            } catch (Exception e) {
                resp.setStatus(500);
                mapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
            }

        } else if ("/logout".equals(path)) {
            Cookie cookie = new Cookie("user_session", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(false);
            resp.addCookie(cookie);
            resp.setStatus(200);
            mapper.writeValue(resp.getWriter(), Map.of("ok", true));
        } else {
            resp.setStatus(404);
            mapper.writeValue(resp.getWriter(), Map.of("error", "Невідомий маршрут"));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCorsHeaders(req, resp);
        resp.setStatus(200);
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
