
# Personal Finance Manager

A Java-based application for managing personal finances, tracking income/expenses, and setting budgets.

---

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing.

### 📋 Prerequisites

Before you begin, ensure you have the following software installed:

* **Java Development Kit (JDK):** Version 11 or higher.
* **Apache Maven:** To build the project and manage dependencies.
* **MySQL Server:** The database for storing all application data.
* **Git:** To clone the repository.

---

## 🛠️ Installation and Setup

Follow these steps to set up the project locally.

### 1. Clone the Repository

First, clone this repository to your local machine.

```bash
# Replace with your repository's URL
git clone [https://github.com/your-username/personal-finance-manager.git](https://github.com/your-username/personal-finance-manager.git)
cd personal-finance-manager
```

2. Set Up the MySQL Database
You must create the database and tables before running the application.

Log in to your MySQL client (e.g., from the command line):

Bash

mysql -u YOUR_USERNAME -p
Create the database named personal_finance_db:

SQL

CREATE DATABASE personal_finance_db;
Select the new database to use it:

SQL

USE personal_finance_db;
Run the following SQL scripts to create all the necessary tables:

SQL

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255)
);

CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(100),
    balance DECIMAL(10,2),
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(100),
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    category_id INT,
    amount DECIMAL(10,2),
    type VARCHAR(10),
    timestamp DATETIME,
    note TEXT,
    created_at DATETIME,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE budgets (
    budget_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    year INT,
    month INT,
    amount DECIMAL(10,2),
    created_at DATETIME,
    UNIQUE(user_id, year, month),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);