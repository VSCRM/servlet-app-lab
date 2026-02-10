package com.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;

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

        Cookie visitCookie = new Cookie("lastVisit", URLEncoder.encode("2026-02-09_14:00", StandardCharsets.UTF_8));
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
            out.println("<h2>Дані з бази даних MySQL (JDBC):</h2>");

            String url = "jdbc:mysql://localhost:3306/mydb";
            String dbUser = "root";
            String dbPass = "admin1";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM users");

                    out.println("<table border='1'><tr><th>ID</th><th>Username</th><th>Password</th></tr>");
                    while (rs.next()) {
                        out.println("<tr>");
                        out.println("<td>" + rs.getInt("id") + "</td>");
                        out.println("<td>" + rs.getString("username") + "</td>");
                        out.println("<td>" + rs.getString("password") + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                }
            } catch (Exception e) {
                out.println("<p style='color:red;'>Помилка JDBC: " + e.getMessage() + "</p>");
            }

            out.println("<hr>");
            out.println("<h2>Додати користувача в БД:</h2>");
            out.println("<form method='POST' action='" + request.getContextPath() + "/user/'>");
            out.println("Логін: <input type='text' name='new_user' required><br>");
            out.println("Пароль: <input type='password' name='new_pass' required><br>");
            out.println("<input type='submit' value='Додати в MySQL'>");
            out.println("</form>");

            out.println("<hr>");
            out.println("<p>Щоб змінити ім'я в сесії, введіть його в URL: <code>/user/НовеІм'я</code></p>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String newUser = request.getParameter("new_user");
        String newPass = request.getParameter("new_pass");

        if (newUser != null && newPass != null) {
            String url = "jdbc:mysql://localhost:3306/mydb";
            String dbUser = "root";
            String dbPass = "admin1";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
                    String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, newUser);
                    pstmt.setString(2, newPass);
                    pstmt.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String nameFromForm = request.getParameter("name");
        if (nameFromForm != null) {
            request.getSession().setAttribute("userName", nameFromForm);
        }

        response.sendRedirect(request.getContextPath() + "/user/");
    }
}