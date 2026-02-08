package com.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String format = request.getParameter("format");
        String pathInfo = request.getPathInfo();
        String userName = (pathInfo != null && pathInfo.length() > 1)
                ? pathInfo.substring(1) : "Guest";

        HttpSession session = request.getSession();
        String savedSessionUser = (String) session.getAttribute("userName");
        if (savedSessionUser == null) {
            session.setAttribute("userName", userName);
            savedSessionUser = userName;
        }

        String lastVisit = "Перший візит";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastVisit".equals(c.getName())) {
                    lastVisit = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                }
            }
        }

        Cookie visitCookie = new Cookie("lastVisit", URLEncoder.encode("2026-02-08_15:40", StandardCharsets.UTF_8));
        visitCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(visitCookie);

        if ("json".equalsIgnoreCase(format)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"status\": \"success\", \"user\": \"" + savedSessionUser + "\", \"lastVisit\": \"" + lastVisit + "\"}");
            out.flush();
        } else {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>Робота з сесіями та куками</h1>");
            out.println("<p>Поточний користувач (з PathVariable): <b>" + userName + "</b></p>");
            out.println("<p>Збережений у сесії: <b>" + savedSessionUser + "</b></p>");
            out.println("<p>Ваш останній візит (з Cookies): <b>" + lastVisit + "</b></p>");
            out.println("<hr>");
            out.println("<p>Щоб змінити ім'я в сесії, введіть його в URL: <code>/user/НовеІм'я</code></p>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nameFromForm = request.getParameter("name");
        if (nameFromForm != null) {
            request.getSession().setAttribute("userName", nameFromForm);
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html><body><h1>Дані оновлено в сесії!</h1>");
        out.println("<a href='user/'>Повернутися</a></body></html>");
    }
}