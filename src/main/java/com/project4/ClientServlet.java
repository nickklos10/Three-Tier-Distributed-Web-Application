package com.project4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Servlet implementation for Client-level queries.
 */
@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String CLIENT_PROPERTIES_PATH = "/WEB-INF/lib/client-level.properties";

    /**
     * Handles POST requests to execute SQL commands submitted by the client-level user.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Session validation
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null || session.getAttribute("role") == null) {
            response.sendRedirect("authentication.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"client".equalsIgnoreCase(role)) {
            response.sendRedirect("unauthorized.jsp");
            return;
        }

        // Get the SQL command from the request
        String sqlCommand = request.getParameter("sqlCommand");
        if (sqlCommand == null || sqlCommand.trim().isEmpty()) {
            sendJsonError(response, "No SQL command provided.");
            return;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();
        try {
            conn = DBConnection.getConnection(getServletContext(), CLIENT_PROPERTIES_PATH);
            stmt = conn.createStatement();

            boolean hasResultSet = stmt.execute(sqlCommand);

            if (hasResultSet) {
                rs = stmt.getResultSet();
                // Convert ResultSet to JSON
                String jsonResult = resultSetToJson(rs);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.write("{\"results\": " + jsonResult + "}");
            } else {
                int updateCount = stmt.getUpdateCount();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.write("{\"updateCount\": " + updateCount + "}");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // For debugging purposes
            sendJsonError(response, "Database error: " + escapeJson(e.getMessage()));
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Converts a ResultSet into a JSON array string.
     */
    private String resultSetToJson(ResultSet rs) throws SQLException {
        StringBuilder json = new StringBuilder();
        json.append("[");

        int columnCount = rs.getMetaData().getColumnCount();
        boolean firstRow = true;
        while (rs.next()) {
            if (!firstRow) {
                json.append(",");
            }
            json.append("{");
            for (int i = 1; i <= columnCount; i++) {
                json.append("\"").append(escapeJson(rs.getMetaData().getColumnLabel(i))).append("\":");
                String value = rs.getString(i);
                if (value == null) {
                    json.append("null");
                } else {
                    json.append("\"").append(escapeJson(value)).append("\"");
                }
                if (i < columnCount) {
                    json.append(",");
                }
            }
            json.append("}");
            firstRow = false;
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Sends a JSON-formatted error message.
     */
    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.write("{\"error\": \"" + escapeJson(message) + "\"}");
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