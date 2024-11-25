# Three tier distributed Web App for an Enterprise system

A servlet/JSP-based multi-tiered enterprise application using a Tomcat container that allows client and root-level users to execute SQL queries and updates with specific business logic implementation.

## Table of Contents
- [Introduction](#introduction)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [Business Logic Implementation](#business-logic-implementation)
- [Screenshots](#screenshots)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Introduction
This project is a multi-tiered enterprise system that demonstrates the use of servlets and JSPs in a Tomcat container. It provides a web interface for client and root-level users to execute SQL commands on a database. The application includes server-side business logic that automatically updates supplier statuses based on certain conditions.

## Technologies Used
- Java EE Servlets
- JavaServer Pages (JSP)
- Apache Tomcat
- JDBC (Java Database Connectivity)
- MySQL Database
- HTML, CSS, JavaScript
- AJAX (Fetch API)
- Bootstrap (optional for styling)

## Features
- **Authentication System**: Users can log in as either client-level or root-level users
- **SQL Command Execution**: Users can execute SQL queries and updates through a web interface
- **Dynamic Result Display**: Query results are displayed dynamically on the same page without reloading
- **Server-Side Business Logic**: Automatically increments the status of suppliers based on shipment quantities
- **Error Handling**: Displays user-friendly error messages for invalid SQL commands or server errors
- **Security**: Session validation to prevent unauthorized access

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- Apache Tomcat 9 or higher
- MySQL Database Server
- Maven (for building the project, if applicable)
- An IDE like Eclipse or IntelliJ IDEA (optional but recommended)

## Installation

### Clone the Repository
```bash
git clone https://github.com/nickklos10/project4-enterprise-system.git
```

### Import the Project into Your IDE
Open your IDE and import the project as a Maven project (if using Maven).

### Build the Project
If using Maven:
```bash
mvn clean install
```
Alternatively, build the project using your IDE's build tools.

## Database Setup

1. Create the Project 4 Database and populate it:
    - Log in to your MySQL server and run the contents of file project4DBscript.sql 

2. Create the Credentials DB and populate it:
    - Run the contents of file credentialsDBscript.sql 

3. Create the users and give permissions:
    - Run the contents of file ClientCreattionPermissionsScript.sql

## Configuration

### Database Configuration Files
Copy the client-level.properties and root-level.properties files to the WEB-INF/lib directory.

Update the database connection details (URL, username, password) in both properties files.

#### client-level.properties
```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/project4_db
jdbc.username=client_user
jdbc.password=client_password
```

#### root-level.properties
```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/project4_db
jdbc.username=root_user
jdbc.password=root_password
```

### Tomcat Configuration
- Deploy the compiled .war file to your Tomcat server's webapps directory
- Alternatively, configure your IDE to deploy the application to Tomcat directly

## Running the Application

### Start the Tomcat Server
Ensure that your Tomcat server is running.

### Access the Application
Open a web browser and navigate to:
```
http://localhost:8080/project4-enterprise-system
```

## Usage

### Client-Level User

#### Login
Navigate to the login page and enter the client-level credentials:
```
Username: client
Password: client_password
```

#### Features
- Can execute SELECT statements
- Limited to client-level database permissions

### Root-Level User

#### Login
Navigate to the login page and enter the root-level credentials:
```
Username: root
Password: root_password
```

#### Features
- Can execute all SQL statements, including INSERT, UPDATE, DELETE
- Has root-level database permissions
- Triggers server-side business logic for certain operations

## Business Logic Implementation

The application implements server-side business logic for the root-level user:

**Condition**: Any INSERT or UPDATE on the shipments table where the quantity is greater than or equal to 100.

**Action**: Increments the status of suppliers directly affected by the operation by 5.

Example:
- Inserting a shipment (S5, P6, J4, 400) increases the status of supplier S5 by 5
- Updating shipments with quantity >= 100 affects only the suppliers involved in those shipments

## Screenshots
[Include screenshots of your application here, such as the login page, client interface, root interface, and examples of query results and business logic notifications.]

## Project Structure
```
project4-enterprise-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.project4/
│   │   │       ├── ClientServlet.java
│   │   │       ├── RootUserServlet.java
│   │   │       ├── DBConnection.java
│   │   │       └── ...
│   │   ├── resources/
│   │   │   ├── client-level.properties
│   │   │   └── root-level.properties
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml
│   │       │   └── lib/
│   │       ├── clientHome.jsp
│   │       ├── root.jsp
│   │       ├── authentication.jsp
│   │       ├── unauthorized.jsp
│   │       └── ...
├── pom.xml
├── README.md
└── ...
```

## Contributing
Contributions are welcome! Please fork the repository and create a pull request with your changes.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
