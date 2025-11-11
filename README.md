
# Personal Finance Manager

A Java-based application for managing personal finances, tracking income/expenses, and setting budgets.

> **✨ NEW**: The GUI has been completely modernized with professional styling, modern colors, and enhanced user experience!

---

## 🎨 GUI Modernization (NEW!)

Your Personal Finance Manager now features a **professional, modern GUI** with:

✅ **Modern Color Scheme** - 11 vibrant, carefully-selected colors  
✅ **Professional Typography** - Segoe UI fonts with proper hierarchy  
✅ **Enhanced Components** - Modern buttons, inputs, panels with smooth interactions  
✅ **Improved Layouts** - Card-based designs with better organization  
✅ **Better UX** - Color-coded buttons, smooth hover effects, validation feedback  
✅ **High Accessibility** - High contrast, larger elements, clear labeling  

### � GUI Documentation

Complete documentation is available for the modernization:

| Document | Purpose |
|----------|---------|
| **[QUICK_START_GUIDE.md](QUICK_START_GUIDE.md)** | Quick overview and common tasks |
| **[INDEX.md](INDEX.md)** | Complete documentation index |
| **[GUI_MODERNIZATION_SUMMARY.txt](GUI_MODERNIZATION_SUMMARY.txt)** | Executive summary of changes |
| **[GUI_BEFORE_AFTER.md](GUI_BEFORE_AFTER.md)** | Visual comparison of improvements |
| **[GUI_STYLE_GUIDE.md](GUI_STYLE_GUIDE.md)** | Design reference with colors and fonts |
| **[GUI_MODERNIZATION.md](GUI_MODERNIZATION.md)** | Technical documentation |
| **[MODERNIZATION_STATUS_REPORT.md](MODERNIZATION_STATUS_REPORT.md)** | Project status and roadmap |
| **[MODERNIZATION_IMPLEMENTATION_GUIDE.md](MODERNIZATION_IMPLEMENTATION_GUIDE.md)** | Guide to modernize remaining panels |

**👉 [Start with QUICK_START_GUIDE.md](QUICK_START_GUIDE.md) to learn about the modernization!**

---

## �🚀 Getting Started

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
git clone https://github.com/your-username/personal-finance-manager.git
cd personal-finance-manager
```

### 2. Set Up the MySQL Database

You must create the database and tables before running the application.

Log in to your MySQL client (e.g., from the command line):

```bash
mysql -u YOUR_USERNAME -p
```

Create the database named personal_finance_db:

```sql
CREATE DATABASE personal_finance_db;
```

Select the new database to use it:

```sql
USE personal_finance_db;
```

Run the following SQL scripts to create all the necessary tables:

```sql
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
```