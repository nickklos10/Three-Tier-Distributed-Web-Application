<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project 4 Enterprise System</title>
    <style>
        body {
            background-color: black;
            color: white;
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .title {
            color: red;
            font-size: 24px;
            text-align: center;
            margin-bottom: 10px;
        }

        .subtitle {
            color: lightblue;
            font-size: 20px;
            text-align: center;
            margin-bottom: 20px;
        }

        .divider {
            width: 100%;
            height: 1px;
            background-color: white;
            margin: 20px 0;
        }

        .info-text {
            color: white;
            font-size: 16px;
            text-align: center;
            margin: 10px 0;
        }

        .options-container {
            width: 45%;
            background-color: #808080;
            padding: 20px;
            margin: 20px 0;
            border-radius: 5px;
        }

        .radio-option {
            margin: 15px 0;
            display: flex;
            align-items: flex-start;
        }

        .radio-option input[type="radio"] {
            margin-top: 5px;
            margin-right: 10px;
        }

        .option-text {
            color: blue;
        }

        .option-description {
            color: black;
            margin-left: 5px;
            display: inline;
        }

        .buttons-container {
            display: flex;
            gap: 20px;
            margin: 20px 0;
        }

        .button {
            padding: 10px 20px;
            border: none;
            cursor: pointer;
            background-color: #006400;
            font-size: 16px;
        }

        .yellow-text {
            color: yellow;
        }

        .red-text {
            color: red;
        }

        .results-text {
            color: white;
            font-size: 18px;
            margin-top: 20px;
        }

        form {
            width: 100%;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        /* Results Display Styles */
        .results-container {
            width: 80%;
            max-width: 800px;
            margin: 20px auto;
            background-color: #1a1a1a;
            border: 1px solid #333;
            border-radius: 5px;
            padding: 20px;
        }

        .result-header {
            color: lightblue;
            font-size: 18px;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid #333;
        }

        .result-timestamp {
            color: #666;
            font-size: 14px;
            margin-bottom: 15px;
        }

        .single-value-result {
            background-color: #2a2a2a;
            padding: 15px;
            border-radius: 4px;
            margin: 10px 0;
        }

        .single-value-result .label {
            color: #ffd700;
            margin-right: 10px;
        }

        .single-value-result .value {
            color: #00ff00;
            font-family: 'Courier New', monospace;
            font-weight: bold;
        }

        .table-result {
            width: 100%;
            border-collapse: collapse;
            margin: 10px 0;
        }

        .table-result th {
            background-color: #333;
            color: lightblue;
            padding: 12px;
            text-align: left;
            font-weight: normal;
        }

        .table-result td {
            padding: 10px;
            border-bottom: 1px solid #333;
            color: #fff;
        }

        .table-result tr:hover {
            background-color: #2a2a2a;
        }

        .list-result {
            list-style: none;
            padding: 0;
            margin: 10px 0;
        }

        .list-result li {
            padding: 10px;
            margin: 5px 0;
            background-color: #2a2a2a;
            border-radius: 4px;
            display: flex;
            justify-content: space-between;
        }

        .list-result .list-label {
            color: #ffd700;
        }

        .list-result .list-value {
            color: #00ff00;
        }

        .error-result {
            background-color: #3a1212;
            color: #ff6b6b;
            padding: 15px;
            border-radius: 4px;
            margin: 10px 0;
            border-left: 4px solid #ff0000;
        }

        .no-results {
            color: #666;
            text-align: center;
            padding: 20px;
            font-style: italic;
        }
    </style>
</head>
<body>
<h1 class="title">Welcome to the Fall 2024 Project 4 Enterprise System</h1>

<h2 class="subtitle">A servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</h2>

<div class="divider"></div>

<p class="info-text">You are connected to the Project 4 Enterprise System database as an accountant-level user.</p>
<p class="info-text">Please select a report option below.</p>

<form action="AccountantServlet" method="post" onsubmit="return handleSubmit(event)">
    <div class="options-container">
        <div class="radio-option">
            <input type="radio" id="option1" name="reportOption" value="maxStatus" required>
            <label for="option1">
                <span class="option-text">Get the maximum status value of all suppliers</span>
                <span class="option-description">(Returns a maximum value)</span>
            </label>
        </div>
        <div class="radio-option">
            <input type="radio" id="option2" name="reportOption" value="totalWeight">
            <label for="option2">
                <span class="option-text">Get the total weight of all parts</span>
                <span class="option-description">(Returns a sum)</span>
            </label>
        </div>
        <div class="radio-option">
            <input type="radio" id="option3" name="reportOption" value="shipmentCount">
            <label for="option3">
                <span class="option-text">Get the total number of shipments</span>
                <span class="option-description">(Returns the current number of shipments in total)</span>
            </label>
        </div>
        <div class="radio-option">
            <input type="radio" id="option4" name="reportOption" value="maxWorkers">
            <label for="option4">
                <span class="option-text">Get the name and number of workers of the job with the most workers</span>
                <span class="option-description">(Returns two values)</span>
            </label>
        </div>
        <div class="radio-option">
            <input type="radio" id="option5" name="reportOption" value="supplierList">
            <label for="option5">
                <span class="option-text">List the name and status of every supplier</span>
                <span class="option-description">(Returns a list of supplier names with status)</span>
            </label>
        </div>
    </div>

    <div class="buttons-container">
        <button type="submit" class="button yellow-text">Execute Command</button>
        <button type="button" class="button red-text" onclick="clearResults()">Clear Results</button>
    </div>
</form>

<p class="info-text">All execution results will appear below this line.</p>

<div class="divider"></div>

<p class="results-text">Execution Results:</p>

<div class="results-container">
    <div class="result-header">Query Results</div>
    <div class="result-timestamp">Executed at: <script>document.write(new Date().toLocaleString())</script></div>
    <div class="no-results">No query has been executed yet.</div>
</div>

<script>
    // Function to clear the results display
    function clearResults() {
        const resultsContainer = document.querySelector('.results-container');
        if (resultsContainer) {
            resultsContainer.innerHTML =
                '<div class="result-header">Query Results</div>' +
                '<div class="result-timestamp">Cleared at: ' + new Date().toLocaleString() + '</div>' +
                '<div class="no-results">Results have been cleared.</div>';
        }
    }

    // Function to display a single value result
    function displaySingleValue(label, value) {
        return '<div class="single-value-result">' +
            '<span class="label">' + escapeHtml(label) + ':</span>' +
            '<span class="value">' + escapeHtml(value) + '</span>' +
            '</div>';
    }

    // Function to display error messages
    function displayError(message) {
        return '<div class="error-result">' + escapeHtml(message) + '</div>';
    }

    // Function to display list results
    function displayList(items) {
        let html = '<ul class="list-result">';
        items.forEach(item => {
            html += '<li>' +
                '<span class="list-label">' + escapeHtml(item.name) + '</span>' +
                '<span class="list-value">Status: ' + escapeHtml(item.status) + '</span>' +
                '</li>';
        });
        html += '</ul>';
        return html;
    }

    // Function to display table results (if needed)
    function displayTable(headers, data) {
        let html = '<table class="table-result"><thead><tr>';

        headers.forEach(header => {
            html += '<th>' + escapeHtml(header) + '</th>';
        });

        html += '</tr></thead><tbody>';

        data.forEach(row => {
            html += '<tr>';
            row.forEach(cell => {
                html += '<td>' + escapeHtml(cell) + '</td>';
            });
            html += '</tr>';
        });

        html += '</tbody></table>';
        return html;
    }

    // Function to update the results container with new content
    function updateResults(content) {
        const resultsContainer = document.querySelector('.results-container');
        if (resultsContainer) {
            resultsContainer.innerHTML =
                '<div class="result-header">Query Results</div>' +
                '<div class="result-timestamp">Executed at: ' + new Date().toLocaleString() + '</div>' +
                content;
        }
    }

    // Function to escape HTML characters to prevent XSS
    function escapeHtml(text) {
        if (text == null) return '';
        return text.replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function handleSubmit(event) {
        event.preventDefault(); // Prevent default form submission
        const formData = new FormData(event.target);
        const reportOption = formData.get('reportOption');

        // Convert formData to URL-encoded string
        const params = new URLSearchParams();
        for (const [key, value] of formData) {
        params.append(key, value);
    }

        fetch('AccountantServlet', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
    },
        body: params.toString()
    })
        .then(response => {
        if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
        return response.json();
    })
        .then(data => {
        if (data.error) {
        updateResults(displayError(data.error));
        return;
    }

        // Handle data based on reportOption
        switch(reportOption) {
        case 'maxStatus':
        updateResults(displaySingleValue('Maximum Supplier Status', data.value));
        break;
        case 'totalWeight':
        updateResults(displaySingleValue('Total Parts Weight', data.value));
        break;
        case 'shipmentCount':
        updateResults(displaySingleValue('Total Shipments', data.value));
        break;
        case 'maxWorkers':
        updateResults(displaySingleValue('Job Name', data.jobName) +
        displaySingleValue('Number of Workers', data.workers));
        break;
        case 'supplierList':
        updateResults(displayList(data.suppliers));
        break;
        default:
        updateResults(displayError('Unknown report option.'));
    }
    })
        .catch(error => {
        updateResults(displayError('Error executing query: ' + error.message));
    });

        return false; // Prevent default form submission
    }
</script>
</body>
</html>