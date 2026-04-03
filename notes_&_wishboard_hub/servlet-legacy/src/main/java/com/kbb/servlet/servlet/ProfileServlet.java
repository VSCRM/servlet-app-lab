package com.kbb.servlet.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@WebServlet(urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

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

        if (val == null || val.isEmpty()) {
            resp.setStatus(200);
            mapper.writeValue(resp.getWriter(), Map.of(
                    "loggedIn", false,
                    "username", "Гість"
            ));
            return;
        }

        String[] parts = val.split(":", 3);

        if (parts.length >= 2) {
            resp.setStatus(200);
            mapper.writeValue(resp.getWriter(), Map.of(
                    "loggedIn",   true,
                    "userId",     Long.parseLong(parts[0]),
                    "username",   parts[1],
                    "sessionId",  parts.length > 2 ? parts[2] : "—",
                    "serverTime", LocalDateTime.now().toString(),
                    "remoteAddr", req.getRemoteAddr(),
                    "userAgent",  req.getHeader("User-Agent") != null
                            ? req.getHeader("User-Agent") : "—"
            ));
        } else {
            resp.setStatus(200);
            mapper.writeValue(resp.getWriter(), Map.of(
                    "loggedIn", false,
                    "username", "Гість"
            ));
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
