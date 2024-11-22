package com.project4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Servlet implementation for Root-level queries.
 */
@WebServlet("/processQuery")
public class RootUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ROOT_PROPERTIES_PATH = "/WEB-INF/lib/root-level.properties";

    /**
     * Handles POST requests to execute SQL commands submitted by the root-level user.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Session validation
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null || session.getAttribute("role") == null) {
            response.sendRedirect("authentication.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"root".equalsIgnoreCase(role)) {
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
            conn = DBConnection.getConnection(getServletContext(), ROOT_PROPERTIES_PATH);
            conn.setAutoCommit(false); // Start transaction
            stmt = conn.createStatement();

            // Before executing the command, get the state of the shipments table
            Set<String> affectedSuppliersBefore = new HashSet<>();

            if (isUpdateShipment(sqlCommand)) {
                // For UPDATE commands, find suppliers that will be directly affected
                affectedSuppliersBefore = getAffectedSuppliers(stmt, sqlCommand);
            }

            boolean hasResultSet = stmt.execute(sqlCommand);
            int updateCount = stmt.getUpdateCount();
            boolean businessLogicApplied = false;
            int suppliersUpdated = 0;

            if (isInsertIntoShipment(sqlCommand)) {
                // For INSERT commands, get the supplier directly affected
                String insertedSupplier = getInsertedSupplier(stmt, sqlCommand);
                if (insertedSupplier != null) {
                    affectedSuppliersBefore.add(insertedSupplier);
                }
            }

            // After executing the command, apply business logic
            if (!affectedSuppliersBefore.isEmpty()) {
                businessLogicApplied = true;
                suppliersUpdated = applyBusinessLogic(conn, affectedSuppliersBefore);
            }

            conn.commit(); // Commit transaction

            if (hasResultSet) {
                rs = stmt.getResultSet();
                // Convert ResultSet to JSON
                String jsonResult = resultSetToJson(rs);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.write("{\"results\": " + jsonResult + "}");
            } else {
                // Build the response JSON
                StringBuilder jsonResponse = new StringBuilder();
                jsonResponse.append("{");
                jsonResponse.append("\"updateCount\": ").append(updateCount).append(",");
                jsonResponse.append("\"message\": \"Command executed successfully.\"");

                if (businessLogicApplied) {
                    jsonResponse.append(",");
                    jsonResponse.append("\"businessLogic\": true,");
                    jsonResponse.append("\"businessMessage\": \"Business logic detected! - Updating supplier status\",");
                    jsonResponse.append("\"suppliersUpdated\": ").append(suppliersUpdated);
                }
                jsonResponse.append("}");

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.write(jsonResponse.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace(); // For debugging purposes
            try {
                if (conn != null) conn.rollback(); // Rollback transaction on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sendJsonError(response, "Database error: " + escapeJson(e.getMessage()));
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Checks if the SQL command is an UPDATE on the shipments table.
     */
    private boolean isUpdateShipment(String sqlCommand) {
        String commandLower = sqlCommand.trim().toLowerCase();
        return commandLower.startsWith("update shipments");
    }

    /**
     * Checks if the SQL command is an INSERT into the shipments table.
     */
    private boolean isInsertIntoShipment(String sqlCommand) {
        String commandLower = sqlCommand.trim().toLowerCase();
        return commandLower.startsWith("insert into shipments");
    }

    /**
     * Gets the supplier number directly affected by an INSERT into shipments.
     */
    private String getInsertedSupplier(Statement stmt, String sqlCommand) throws SQLException {
        // This is a simplified approach and assumes standard SQL syntax
        // For production code, consider using a SQL parser library

        // Extract values from the INSERT command
        String lowerCmd = sqlCommand.trim().toLowerCase();
        int valuesIndex = lowerCmd.indexOf("values");
        if (valuesIndex == -1) {
            return null;
        }
        String valuesPart = sqlCommand.substring(valuesIndex + 6).trim();
        if (valuesPart.startsWith("(") && valuesPart.endsWith(")")) {
            valuesPart = valuesPart.substring(1, valuesPart.length() - 1);
        }

        // Split the values and extract the supplier number (assuming it's the first value)
        String[] values = valuesPart.split(",");
        if (values.length < 1) {
            return null;
        }
        String snum = values[0].trim();
        // Remove quotes if present
        if (snum.startsWith("'") && snum.endsWith("'")) {
            snum = snum.substring(1, snum.length() - 1);
        }

        return snum;
    }

    /**
     * Gets the supplier numbers directly affected by an UPDATE on shipments.
     */
    private Set<String> getAffectedSuppliers(Statement stmt, String sqlCommand) throws SQLException {
        // To identify affected suppliers, we need to run a SELECT query similar to the UPDATE command
        // but selecting the supplier numbers before the update

        // This is a simplistic and limited approach; for complex SQL statements, a full SQL parser would be needed

        // Extract the WHERE clause
        String lowerCmd = sqlCommand.trim().toLowerCase();
        int whereIndex = lowerCmd.indexOf("where");
        String whereClause = "";
        if (whereIndex != -1) {
            whereClause = sqlCommand.substring(whereIndex);
        }

        // Build a SELECT query to get affected supplier numbers
        String selectSuppliersSQL = "SELECT DISTINCT snum FROM shipments " + whereClause;
        ResultSet rs = stmt.executeQuery(selectSuppliersSQL);

        Set<String> suppliers = new HashSet<>();
        while (rs.next()) {
            suppliers.add(rs.getString("snum"));
        }
        rs.close();

        return suppliers;
    }

    /**
     * Applies business logic by updating supplier statuses and returns the number of suppliers updated.
     */
    private int applyBusinessLogic(Connection conn, Set<String> affectedSuppliers) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            // Check if the affected suppliers have shipments with quantity >= 100 after the update
            String supplierList = String.join(",", Collections.nCopies(affectedSuppliers.size(), "?"));
            String checkSuppliersSQL = "SELECT DISTINCT s.snum FROM suppliers s " +
                    "JOIN shipments sh ON s.snum = sh.snum " +
                    "WHERE sh.quantity >= 100 AND s.snum IN (" + supplierList + ")";
            pstmt = conn.prepareStatement(checkSuppliersSQL);

            int index = 1;
            for (String snum : affectedSuppliers) {
                pstmt.setString(index++, snum);
            }

            ResultSet rs = pstmt.executeQuery();
            Set<String> suppliersToUpdate = new HashSet<>();
            while (rs.next()) {
                suppliersToUpdate.add(rs.getString("snum"));
            }
            rs.close();
            pstmt.close();

            if (suppliersToUpdate.isEmpty()) {
                return 0;
            }

            // Update the status of these suppliers by incrementing by 5
            String updateStatusSQL = "UPDATE suppliers SET status = status + 5 WHERE snum IN (" +
                    String.join(",", Collections.nCopies(suppliersToUpdate.size(), "?")) + ")";
            pstmt = conn.prepareStatement(updateStatusSQL);

            index = 1;
            for (String snum : suppliersToUpdate) {
                pstmt.setString(index++, snum);
            }

            int suppliersUpdated = pstmt.executeUpdate();

            return suppliersUpdated;

        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
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