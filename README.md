# PUP Book Borrowing System

**Workshop Project — Might Malinay, BSIT 2-2**  
Polytechnic University of the Philippines

A simple desktop application for managing library book borrowing transactions. Built with JavaFX (MVC) and PostgreSQL.

---

## Features

- **Login screen** — BCrypt-hashed password authentication
- **Dashboard** — stat cards (books, availability, borrows, students) + full CRUD book management with search
- **Borrow Records** — create transactions, mark returns, filter by status, auto-flag overdue records
- Credentials and database config loaded from `.env` (never hardcoded)
- Pure JavaFX UI — no HTML/CSS/web dependencies

---

## Tech Stack

| Layer      | Technology                     |
|------------|--------------------------------|
| UI         | JavaFX 21                      |
| Language   | Java 17                        |
| Database   | PostgreSQL 15+                 |
| JDBC       | postgresql 42.7.3              |
| Security   | jBCrypt 0.4                    |
| Env config | dotenv-java 3.0.0              |
| Build      | Maven 3.9+                     |

---

## Prerequisites

- Java 17+ (JDK) — [Download](https://adoptium.net/)
- Maven 3.9+ — [Download](https://maven.apache.org/download.cgi)
- PostgreSQL 15+ — [Download](https://www.postgresql.org/download/)

---

## 1 — Clone the project

```bash
git clone https://github.com/mXyrel/Workshop.git
cd Workshop
```

---

## 2 — Environment setup

```bash
cp .env.example .env
```

Edit `.env` with your PostgreSQL credentials:

```
DB_URL=jdbc:postgresql://localhost:5432/workshop_db
DB_USER=postgres
DB_PASSWORD=your_password
```

---

## 3 — Database setup

```bash
# Create the database
psql -U postgres -c "CREATE DATABASE workshop_db;"

# Run the schema + sample data script
psql -U postgres -d workshop_db -f sql/init.sql
```

This creates four tables (`users`, `students`, `books`, `borrow_records`) and populates them with sample data, including the default user accounts.

If you need to recreate or add users later from Java, use PowerShell-safe syntax:

```bash
mvn --% -Dexec.mainClass=com.workshop.bbs.util.SetupUsers exec:java
```

If you are not in PowerShell, the same command also works without `--%`:

```bash
mvn -Dexec.mainClass=com.workshop.bbs.util.SetupUsers exec:java
```

---

## 4 — Run the application

```bash
# Option A — JavaFX Maven plugin (recommended)
mvn javafx:run

# Option B — exec plugin
mvn compile exec:java

# Option C — VS Code
# Open the project folder, install "Extension Pack for Java", then click Run on MainApp.java
```

---

## Default Login

| Username | Password  | Role      |
|----------|-----------|-----------|
| admin    | admin123  | Admin     |
| libuser  | lib123    | Librarian |
| malinay  | admin123  | Admin     |

---

## Project Structure

```
book-borrowing-system/
├── pom.xml
├── .env                   ← your local credentials (gitignored)
├── .env.example           ← template for GitHub
├── .gitignore
├── sql/
│   └── init.sql           ← schema + sample data
└── src/main/java/com/workshop/bbs/
    ├── MainApp.java        ← application entry point
    ├── controller/
    │   ├── LoginController.java     ← Scene 1
    │   ├── DashboardController.java ← Scene 2
    │   └── RecordsController.java   ← Scene 3
    ├── dao/
    │   ├── UserDAO.java
    │   ├── BookDAO.java
    │   ├── StudentDAO.java
    │   └── BorrowRecordDAO.java
    ├── model/
    │   ├── User.java
    │   ├── Book.java
    │   ├── Student.java
    │   └── BorrowRecord.java
    ├── util/
    │   ├── DatabaseConnection.java
    │   ├── PasswordUtil.java
    │   └── Session.java
    └── view/
        ├── SceneManager.java
        └── Styles.java
```


---

## Security Notes

- Passwords are hashed with BCrypt (cost factor 12) — never stored in plain text
- Database credentials are loaded from `.env` — never hardcoded in source
- `.env` is listed in `.gitignore` — safe to use a `.env.example` for sharing setup

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `Missing required environment variable: DB_URL` | Make sure `.env` exists in the project root |
| `FATAL: database "workshop_db" does not exist` | Run `psql -U postgres -c "CREATE DATABASE workshop_db;"` |
| `Connection refused` | Ensure PostgreSQL service is running |
| JavaFX module errors | Make sure you use `mvn javafx:run`, not plain `java -jar` |
