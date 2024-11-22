<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Project 4 Enterprise System - Client User</title>
  <style>
    /* Common styles */
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
      color: yellow;
      font-size: 24px;
      text-align: center;
      margin-bottom: 10px;
    }

    .subtitle {
      color: #00ff00;
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

    .sql-input {
      width: 70%;
      height: 200px;
      background-color: #0000ff;
      color: white;
      padding: 10px;
      margin: 20px 0;
      border: none;
      resize: none;
      font-family: monospace;
      font-size: 16px;
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
      color: white;
    }

    .yellow-text {
      color: yellow;
    }

    .red-text {
      color: red;
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

<p class="info-text">You are connected to the Project 4 Enterprise System database as a client-level user.</p>
<p class="info-text">Please enter any SQL query or update command in the box below.</p>

<form id="sqlQueryForm" onsubmit="return handleSubmit(event)">
  <textarea class="sql-input" id="sqlQueryInput" name="sqlCommand" placeholder="Enter your SQL command here"></textarea>

  <div class="buttons-container">
    <button type="submit" class="button yellow-text">Execute Command</button>
    <button type="reset" class="button red-text">Reset Form</button>
    <button type="button" class="button yellow-text" onclick="clearResults()">Clear Results</button>
  </div>
</form>

<p class="info-text">All execution results will appear below this line.</p>

<div class="divider"></div>

<div class="results-container">
  <div class="result-header">Query Results</div>
  <div class="result-timestamp" id="resultTimestamp">No query executed yet.</div>
  <div class="result-content" id="queryResultDisplay">
    <div class="no-results">Waiting for query execution...</div>
  </div>
</div>

<script>
  // Function to clear the results display
  function clearResults() {
    const resultTimestamp = document.getElementById('resultTimestamp');
    const queryResultDisplay = document.getElementById('queryResultDisplay');
    resultTimestamp.textContent = 'Results cleared at: ' + new Date().toLocaleString();
    queryResultDisplay.innerHTML = '<div class="no-results">Results have been cleared.</div>';
  }

  // Function to escape HTML to prevent XSS
  function escapeHtml(text) {
    if (text == null) return '';
    return text.toString().replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
  }

  // Function to display error messages
  function displayError(message) {
    return '<div class="error-result">' + escapeHtml(message) + '</div>';
  }

  // Function to display table results
  function displayTable(headers, data) {
    let html = '<table class="table-result"><thead><tr>';

    headers.forEach(header => {
      html += '<th>' + escapeHtml(header) + '</th>';
    });

    html += '</tr></thead><tbody>';

    data.forEach(row => {
      html += '<tr>';
      headers.forEach(header => {
        let cellValue = row[header];
        if (cellValue === null || cellValue === undefined) {
          cellValue = '';
        }
        html += '<td>' + escapeHtml(cellValue) + '</td>';
      });
      html += '</tr>';
    });

    html += '</tbody></table>';
    return html;
  }

  // Function to update the results container with new content
  function updateResults(content) {
    const resultTimestamp = document.getElementById('resultTimestamp');
    const queryResultDisplay = document.getElementById('queryResultDisplay');
    resultTimestamp.textContent = 'Executed at: ' + new Date().toLocaleString();
    queryResultDisplay.innerHTML = content;
  }

  function handleSubmit(event) {
    event.preventDefault(); // Prevent default form submission

    const sqlQuery = document.getElementById('sqlQueryInput').value.trim();

    // Basic query validation
    if (!sqlQuery) {
      alert('Please enter a SQL query.');
      return false;
    }

    // Create URL-encoded data
    const params = new URLSearchParams();
    params.append('sqlCommand', sqlQuery); // Use 'sqlCommand' as the parameter name

    // Send the query to the server using fetch
    fetch('ClientServlet', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: params.toString(),
    })
            .then(response => response.text()) // Expecting text (HTML or JSON)
            .then(text => {
              let data;
              try {
                data = JSON.parse(text); // Try to parse as JSON
              } catch (e) {
                // Not JSON, assume it's HTML
                updateResults(text);
                return;
              }

              // If data is JSON, handle accordingly
              if (data.error) {
                updateResults(displayError(data.error));
              } else if (data.updateCount !== undefined) {
                updateResults('<div class="single-value-result">' +
                        '<span class="label">Command executed successfully. Rows affected:</span>' +
                        '<span class="value">' + data.updateCount + '</span>' +
                        '</div>');
              } else if (Array.isArray(data.results) && data.results.length > 0) {
                const headers = Object.keys(data.results[0]);
                updateResults(displayTable(headers, data.results));
              } else {
                updateResults('<div class="no-results">Query executed successfully, but no results were returned.</div>');
              }
            })
            .catch(error => {
              updateResults(displayError('Error executing query: ' + error.message));
            });

    return false; // Prevent form submission
  }
</script>
</body>
</html>