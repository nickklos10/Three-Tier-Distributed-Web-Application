package com.project4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AuthenticationServlet")
public class AuthenticationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Path to the authentication-level properties file
    private static final String AUTHENTICATION_PROPERTIES_PATH = "/WEB-INF/lib/system-level.properties";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve and trim username and password from request parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate input
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            // Redirect to error page with error message
            request.setAttribute("errorMessage", "Username and password must not be empty.");
            request.getRequestDispatcher("errorpage.jsp").forward(request, response);
            return;
        }

        username = username.trim();
        password = password.trim();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Establish database connection
            conn = DBConnection.getConnection(getServletContext(), AUTHENTICATION_PROPERTIES_PATH);
            stmt = conn.createStatement();

            // Execute query to verify user credentials
            String query = "SELECT login_username FROM usercredentials WHERE login_username = '" + escapeSql(username) +
                    "' AND login_password = '" + escapeSql(password) + "'";
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                // Authentication successful
                // Assign role based on username
                String role = getUserRole(username);

                // If role is unknown, deny access
                if (role.equals("unknown")) {
                    // Redirect to error page with error message
                    request.setAttribute("errorMessage", "Unauthorized user.");
                    request.getRequestDispatcher("errorpage.jsp").forward(request, response);
                    return;
                }

                // Create session and set attributes
                HttpSession session = request.getSession(true);
                session.setAttribute("username", username);
                session.setAttribute("role", role);

                // Redirect to the appropriate page based on role
                if (role.equals("root")) {
                    response.sendRedirect("rootHome.jsp");
                } else if (role.equals("client")) {
                    response.sendRedirect("clientHome.jsp");
                } else if (role.equals("accountant")) {
                    response.sendRedirect("AccountantUserApp.jsp");
                } else {
                    // Unknown role, redirect to error page
                    request.setAttribute("errorMessage", "Unauthorized user.");
                    request.getRequestDispatcher("errorpage.jsp").forward(request, response);
                }
            } else {
                // Authentication failed
                // Redirect to error page with error message
                request.setAttribute("errorMessage", "Invalid username or password.");
                request.getRequestDispatcher("errorpage.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // For debugging purposes
            // Redirect to error page with error message
            request.setAttribute("errorMessage", "Database error: " + escapeJson(e.getMessage()));
            request.getRequestDispatcher("errorpage.jsp").forward(request, response);
        } finally {
            // Close resources in reverse order of their opening
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Assigns user role based on username.
     */
    private String getUserRole(String username) {
        username = username.trim();
        if (username.equalsIgnoreCase("root")) {
            return "root";
        } else if (username.equalsIgnoreCase("client")) {
            return "client";
        } else if (username.equalsIgnoreCase("theaccountant")) {
            return "accountant";
        } else {
            return "unknown";
        }
    }

    /**
     * Escapes SQL special characters to prevent SQL injection.
     */
    private String escapeSql(String str) {
        if (str == null) return "";
        return str.replace("'", "''");
    }

    /**
     * Escapes JSON special characters to prevent JSON injection.
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("/", "\\/")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}