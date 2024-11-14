Follow the Steps to run the Chat Application (You can either use Vs Code or IntelliJ):

Step 1:[1st Terminal]
javac Server.java,  
java Server  

Step 2: [2nd Terminal]
javac Client.java,  
javac CreateUser.java,  
java CreateUser  

1. Install Necessary Tools
Ensure you have the following installed on your computer:

Java Development Kit (JDK): At least version 8 or above.
MySQL Database: Installed and running.
Text Editor or IDE: Such as IntelliJ IDEA, Eclipse, or Visual Studio Code.
2. Set Up the MySQL Database
Open MySQL Workbench or any MySQL client tool.
Execute the following SQL commands to set up the database:
CREATE DATABASE chat_app;

USE chat_app;

CREATE TABLE files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(10),
    content LONGBLOB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

Replace the DB_USER and DB_PASSWORD in DatabaseHelper.java with your MySQL username and password:
java
Copy code
private static final String DB_USER = "your_mysql_username";
private static final String DB_PASSWORD = "your_mysql_password";


private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
