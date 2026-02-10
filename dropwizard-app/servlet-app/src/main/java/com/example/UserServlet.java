package com.example;

import com.example.dao.UserDAO;
import com.example.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MyDB");
            userDAO = new UserDAO(ds);
        } catch (NamingException e) {
            throw new ServletException("DataSource not found", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        out.println("<html><body><h1>Управління користувачами (Lab 3)</h1>");

        out.println("<form method='GET' action='" + request.getContextPath() + "/user/search'>");
        out.println("Пошук за ID: <input type='number' name='id' required>");
        out.println("<input type='submit' value='Знайти'>");
        out.println("</form><hr>");

        try {
            if (pathInfo != null && pathInfo.equals("/search")) {
                int id = Integer.parseInt(request.getParameter("id"));
                User user = userDAO.getUserById(id);
                if (user != null) {
                    out.println("<h3>Знайдено: ID " + user.getId() + " | " + user.getName() + " | " + user.getEmail() + "</h3>");
                } else {
                    out.println("<p style='color:red;'>Користувача з ID " + id + " не знайдено</p>");
                }
                out.println("<a href='" + request.getContextPath() + "/user/'>Назад до списку</a>");
            } else if (pathInfo != null && pathInfo.equals("/delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                userDAO.deleteUser(id);
                response.sendRedirect(request.getContextPath() + "/user/");
                return;
            } else if (pathInfo != null && pathInfo.equals("/edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                User user = userDAO.getUserById(id);
                out.println("<h2>Редагування (Транзакція)</h2>");
                out.println("<form method='POST' action='" + request.getContextPath() + "/user/update'>");
                out.println("<input type='hidden' name='id' value='" + user.getId() + "'>");
                out.println("Ім'я: <input type='text' name='username' value='" + user.getName() + "'><br>");
                out.println("Email: <input type='email' name='email' value='" + user.getEmail() + "'><br>");
                out.println("<input type='submit' value='Оновити'>");
                out.println("</form>");
            } else {
                List<User> users = userDAO.getAllUsers();
                out.println("<table border='1'><tr><th>ID</th><th>Username</th><th>Email</th><th>Дії</th></tr>");
                for (User u : users) {
                    out.println("<tr>");
                    out.println("<td>" + u.getId() + "</td>");
                    out.println("<td>" + u.getName() + "</td>");
                    out.println("<td>" + u.getEmail() + "</td>");
                    out.println("<td>");
                    out.println("<a href='" + request.getContextPath() + "/user/edit?id=" + u.getId() + "'>Редагувати</a> | ");
                    out.println("<a href='" + request.getContextPath() + "/user/delete?id=" + u.getId() + "' onclick='return confirm(\"Видалити?\")'>Видалити</a>");
                    out.println("</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>Помилка: " + e.getMessage() + "</p>");
        }

        if (pathInfo == null || (!pathInfo.equals("/edit") && !pathInfo.equals("/search"))) {
            out.println("<hr><h2>Додати нового:</h2>");
            out.println("<form method='POST' action='" + request.getContextPath() + "/user/add'>");
            out.println("Логін: <input type='text' name='new_user' required><br>");
            out.println("Email: <input type='email' name='new_email' required><br>");
            out.println("Пароль: <input type='password' name='new_pass' required><br>");
            out.println("<input type='submit' value='Додати'>");
            out.println("</form>");
        }

        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.equals("/add")) {
                String name = request.getParameter("new_user");
                String email = request.getParameter("new_email");
                String pass = request.getParameter("new_pass");
                if (name != null && email != null) {
                    userDAO.createUser(new User(name, email, pass));
                }
            } else if (pathInfo != null && pathInfo.equals("/update")) {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("username");
                String email = request.getParameter("email");
                User user = new User();
                user.setId(id);
                user.setName(name);
                user.setEmail(email);
                userDAO.updateUserWithTransaction(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect(request.getContextPath() + "/user/");
    }
}