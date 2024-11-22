<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error Page</title>
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

        .header {
            background-color: blue;
            color: yellow;
            padding: 1.5rem 2rem;
            font-size: 1.2rem;
            font-weight: bold;
            margin-bottom: 3rem;
        }

        .error-message {
            text-align: center;
        }

        .error-message h1 {
            color: red;
            font-size: 4rem;
            margin-bottom: 2rem;
        }

        .error-message p {
            color: green;
            font-size: 1.2rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
<div class="header">
    CNT4714 Fall 2024 - Enterprise System
</div>
<div class="error-message">
    <h1>Authentication Error</h1>
    <p>"Username and password not Recognized"</p>
    <p>Access to system is denied</p>
    <p>Please try again later</p>
</div>
</body>
</html>