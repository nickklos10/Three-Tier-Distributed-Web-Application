package com.project4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation for Accountant-level report queries.
 */
@WebServlet("/AccountantServlet")
public class AccountantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Path to the accountant-level properties file
    private static final String ACCOUNTANT_PROPERTIES_PATH = "/WEB-INF/lib/accountant-level.properties";

    /**
     * Handles POST requests to execute SQL queries based on selected report options.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Session validation
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null || session.getAttribute("role") == null) {
            response.sendRedirect("authentication.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"accountant".equalsIgnoreCase(role)) {
            response.sendRedirect("errorpage.jsp");
            return;
        }

        String reportOption = request.getParameter("reportOption");

        // Set response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (reportOption == null || reportOption.trim().isEmpty()) {
            sendJsonError(response, "No report option specified.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();
        try {
            conn = DBConnection.getConnection(getServletContext(), ACCOUNTANT_PROPERTIES_PATH);

            // Map report options to SQL queries
            String sqlQuery = mapReportOptionToQuery(reportOption);
            if (sqlQuery == null) {
                sendJsonError(response, "Invalid report option.");
                return;
            }

            pstmt = conn.prepareStatement(sqlQuery);
            rs = pstmt.executeQuery();

            // Process the result set based on the report option
            String jsonResult = processResultSet(reportOption, rs);
            out.write(jsonResult);

        } catch (SQLException e) {
            e.printStackTrace(); // For debugging purposes
            sendJsonError(response, "Database error: " + escapeJson(e.getMessage()));
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Maps report options to SQL queries.
     */
    private String mapReportOptionToQuery(String reportOption) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("maxStatus", "SELECT MAX(status) AS max_status FROM suppliers");
        queryMap.put("totalWeight", "SELECT SUM(weight) AS total_weight FROM parts");
        queryMap.put("shipmentCount", "SELECT COUNT(*) AS shipment_count FROM shipments");
        queryMap.put("maxWorkers", "SELECT jname, numworkers FROM jobs ORDER BY numworkers DESC LIMIT 1");
        queryMap.put("supplierList", "SELECT sname AS name, status FROM suppliers ORDER BY status DESC");

        return queryMap.get(reportOption);
    }

    /**
     * Processes the ResultSet and returns a JSON string based on the report option.
     */
    private String processResultSet(String reportOption, ResultSet rs) throws SQLException {
        StringBuilder json = new StringBuilder();
        json.append("{");

        if ("maxStatus".equals(reportOption) || "totalWeight".equals(reportOption) || "shipmentCount".equals(reportOption)) {
            if (rs.next()) {
                String value = rs.getString(1);
                json.append("\"value\": \"").append(escapeJson(value)).append("\"");
            } else {
                json.append("\"value\": null");
            }
        } else if ("maxWorkers".equals(reportOption)) {
            if (rs.next()) {
                String jobName = rs.getString("jname");
                String workers = rs.getString("numworkers");
                json.append("\"jobName\": \"").append(escapeJson(jobName)).append("\", ");
                json.append("\"workers\": \"").append(escapeJson(workers)).append("\"");
            } else {
                json.append("\"jobName\": null, \"workers\": null");
            }
        } else if ("supplierList".equals(reportOption)) {
            json.append("\"suppliers\": ").append(suppliersToJson(rs));
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Converts the suppliers ResultSet into a JSON array string.
     */
    private String suppliersToJson(ResultSet rs) throws SQLException {
        StringBuilder jsonArray = new StringBuilder();
        jsonArray.append("[");

        boolean first = true;
        while (rs.next()) {
            if (!first) {
                jsonArray.append(", ");
            }
            jsonArray.append("{")
                    .append("\"name\": \"").append(escapeJson(rs.getString("name"))).append("\", ")
                    .append("\"status\": \"").append(escapeJson(rs.getString("status"))).append("\"")
                    .append("}");
            first = false;
        }

        jsonArray.append("]");
        return jsonArray.toString();
    }

    /**
     * Sends a JSON-formatted error message.
     */
    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
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