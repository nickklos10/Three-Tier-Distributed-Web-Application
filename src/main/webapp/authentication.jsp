<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Authentication Page</title>
    <style>
        body {
            background-color: black;
            color: white;
            font-family: Arial, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }

        h1 {
            color: yellow;
            font-size: 2rem;
            margin-bottom: 0.5rem;
            text-align: center;
        }

        h2 {
            color: green;
            font-size: 1.5rem;
            margin-bottom: 1.5rem;
            text-align: center;
        }

        h3 {
            color: red;
            font-size: 1.5rem;
            margin-bottom: 2rem;
        }

        .login-container {
            background-color: rgba(255, 255, 255, 0.1);
            padding: 2rem;
            border-radius: 0.5rem;
            width: 300px;
            text-align: left;
        }

        .login-container label {
            display: block;
            margin-bottom: 0.5rem;
        }

        .login-container input {
            width: 100%;
            padding: 0.5rem;
            margin-bottom: 1rem;
            background-color: rgba(255, 255, 255, 0.2);
            border: none;
            color: white;
        }


        .login-container button {
            background-color: green;
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            font-size: 1rem;
            cursor: pointer;
            width: 100%;
        }

        /* Error Message Styling */
        .error-message {
            color: red;
            margin-top: 1rem;
            text-align: center;
        }
    </style>
</head>
<body>
<h1>Welcome to the Fall 2024 Project 4 Enterprise System</h1>
<h2>A servlet/JSP-based Multi-tiered Enterprise Application Using a Tomcat Container</h2>
<h3>-User Authentication Page-</h3>
<div class="login-container">
    <form action="AuthenticationServlet" method="post">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" placeholder="Enter your username" required>
        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Enter your password" required>
        <button type="submit">Click to authenticate</button>
    </form>
    <!-- Placeholder for dynamic error messages -->
    <div class="error-message">
        <%-- JSP scriptlet to display errors if any --%>
        <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
        out.print(error);
        }
        %>
    </div>
</div>
</body>
</html>
