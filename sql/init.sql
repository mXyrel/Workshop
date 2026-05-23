-- ==========================================================
-- PUP Book Borrowing System — Database Setup Script
-- Workshop Project · Might Malinay, BSIT 2-2
-- ==========================================================
-- SETUP ORDER:
--   1. psql -U postgres -c "CREATE DATABASE workshop_db;"
--   2. psql -U postgres -d workshop_db -f sql/init.sql
--   3. mvn javafx:run
--
-- Default users are now seeded in this script, so the SetupUsers step is optional.
-- If you want to recreate or add users later from PowerShell, use:
--   mvn --% -Dexec.mainClass=com.workshop.bbs.util.SetupUsers compile exec:java
-- ==========================================================

DROP TABLE IF EXISTS borrow_records CASCADE;
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ── Users ────────────────────────────────────────────────
CREATE TABLE users (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'librarian',
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ── Default users ───────────────────────────────────────────
INSERT INTO users (username, password_hash, full_name, role) VALUES
    ('admin',   '$2a$12$FCTh90FpZa0CBzoozOn8IebLfniTbP/wfOkW1lhHwRgzQYpMsL2K.', 'Administrator', 'admin'),
    ('libuser', '$2a$12$/Z05CsQ9LDw7uOZXF/oll.K4XNo/L1GcdIZiTZXKfx9LXgpxOMx4S', 'Maria Santos', 'librarian'),
    ('malinay', '$2a$12$gT87.AuxPeDyFVhE1yiuZOSny4pMg5NlmMk6.t8/01YIuJ1hnZLhm', 'Might Malinay', 'admin');

-- ── Students ─────────────────────────────────────────────
CREATE TABLE students (
    id             SERIAL PRIMARY KEY,
    student_number VARCHAR(20)  NOT NULL UNIQUE,
    full_name      VARCHAR(100) NOT NULL,
    course         VARCHAR(50)  NOT NULL,
    year_level     VARCHAR(10)  NOT NULL,
    email          VARCHAR(100),
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ── Books ────────────────────────────────────────────────
CREATE TABLE books (
    id               SERIAL PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    author           VARCHAR(100) NOT NULL,
    isbn             VARCHAR(20),
    total_copies     INT          NOT NULL DEFAULT 1,
    available_copies INT          NOT NULL DEFAULT 1,
    category         VARCHAR(50),
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
);

-- ── Borrow Records ───────────────────────────────────────
CREATE TABLE borrow_records (
    id          SERIAL PRIMARY KEY,
    student_id  INT         NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    book_id     INT         NOT NULL REFERENCES books(id)    ON DELETE CASCADE,
    borrow_date DATE        NOT NULL DEFAULT CURRENT_DATE,
    due_date    DATE        NOT NULL,
    return_date DATE,
    status      VARCHAR(20) NOT NULL DEFAULT 'borrowed'
                CHECK (status IN ('borrowed', 'returned', 'overdue')),
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_borrow_student ON borrow_records(student_id);
CREATE INDEX idx_borrow_book    ON borrow_records(book_id);
CREATE INDEX idx_borrow_status  ON borrow_records(status);

-- ── Students sample data ─────────────────────────────────
INSERT INTO students (student_number, full_name, course, year_level, email) VALUES
    ('2021-00001', 'Juan Dela Cruz',       'BSIT', '3rd Year', 'juan.delacruz@pup.edu.ph'),
    ('2021-00002', 'Maria Clara Santos',   'BSCS', '2nd Year', 'maria.santos@pup.edu.ph'),
    ('2022-00003', 'Jose Rizal Mendoza',   'BSIT', '2nd Year', 'jose.mendoza@pup.edu.ph'),
    ('2022-00004', 'Andres Bonifacio Go',  'BSCPE','1st Year', 'andres.go@pup.edu.ph'),
    ('2020-00005', 'Gabriela Silang Cruz', 'BSIT', '4th Year', 'gabriela.cruz@pup.edu.ph'),
    ('2023-00006', 'Emilio Aguinaldo Tan', 'BSBA', '1st Year', 'emilio.tan@pup.edu.ph'),
    ('2021-00007', 'Melchora Aquino Lim',  'BSIT', '3rd Year', 'melchora.lim@pup.edu.ph'),
    ('2022-00008', 'Lapu Lapu Reyes',      'BSME', '2nd Year', 'lapulapu.reyes@pup.edu.ph');

-- ── Books sample data ────────────────────────────────────
INSERT INTO books (title, author, isbn, total_copies, available_copies, category) VALUES
    ('Introduction to Java Programming',          'Y. Daniel Liang',     '978-0-13-376131-3', 3, 3, 'Programming'),
    ('Database System Concepts',                  'Silberschatz et al.', '978-0-07-802215-9', 2, 2, 'Database'),
    ('Clean Code',                                'Robert C. Martin',    '978-0-13-235088-4', 2, 2, 'Software Engineering'),
    ('The Pragmatic Programmer',                  'Hunt & Thomas',       '978-0-20-161622-4', 2, 2, 'Software Engineering'),
    ('Computer Networks',                         'Andrew Tanenbaum',    '978-0-13-212695-3', 2, 2, 'Networking'),
    ('Operating System Concepts',                 'Silberschatz et al.', '978-1-11-806333-0', 3, 3, 'Operating Systems'),
    ('Discrete Mathematics',                      'Kenneth Rosen',       '978-0-07-288008-3', 2, 2, 'Mathematics'),
    ('Artificial Intelligence: A Modern Approach','Russell & Norvig',    '978-0-13-604259-4', 1, 1, 'AI/ML'),
    ('Web Development with Node.js',              'Shelley Powers',      '978-1-49-195486-2', 2, 2, 'Web Development'),
    ('Data Structures and Algorithms in Java',    'Robert Lafore',       '978-0-67-232369-0', 2, 2, 'Programming'),
    ('Software Engineering',                      'Ian Sommerville',     '978-0-13-394303-0', 2, 2, 'Software Engineering'),
    ('Computer Organization and Architecture',    'William Stallings',   '978-0-13-293633-0', 1, 1, 'Hardware'),
    ('Python Crash Course',                       'Eric Matthes',        '978-1-59-327603-4', 3, 3, 'Programming'),
    ('The Art of Computer Programming Vol 1',     'Donald Knuth',        '978-0-20-183803-8', 1, 1, 'Mathematics'),
    ('Design Patterns',                           'Gang of Four',        '978-0-20-163361-5', 2, 2, 'Software Engineering');

-- ── Borrow records sample data ───────────────────────────
INSERT INTO borrow_records (student_id, book_id, borrow_date, due_date, return_date, status) VALUES
    (1, 1, CURRENT_DATE - 10, CURRENT_DATE + 4,  NULL,              'borrowed'),
    (2, 3, CURRENT_DATE - 5,  CURRENT_DATE + 9,  NULL,              'borrowed'),
    (3, 5, CURRENT_DATE - 20, CURRENT_DATE - 6,  NULL,              'overdue'),
    (4, 2, CURRENT_DATE - 8,  CURRENT_DATE + 6,  NULL,              'borrowed'),
    (5, 4, CURRENT_DATE - 15, CURRENT_DATE - 1,  CURRENT_DATE - 1,  'returned'),
    (6, 6, CURRENT_DATE - 3,  CURRENT_DATE + 11, NULL,              'borrowed'),
    (7, 8, CURRENT_DATE - 30, CURRENT_DATE - 16, CURRENT_DATE - 18, 'returned'),
    (1, 9, CURRENT_DATE - 2,  CURRENT_DATE + 12, NULL,              'borrowed');

-- Reflect active borrows in available_copies
UPDATE books SET available_copies = available_copies - 1 WHERE id IN (1, 2, 3, 5, 6, 9);

-- Summary
SELECT 'students' AS tbl, COUNT(*) AS rows FROM students
UNION ALL SELECT 'books',          COUNT(*) FROM books
UNION ALL SELECT 'borrow_records', COUNT(*) FROM borrow_records;

-- NOTE: Default users are now seeded in this script.
-- Default accounts:
--   admin   / admin123
--   libuser / lib123
--   malinay / admin123
-- You can still run SetupUsers if you want to recreate or add users separately:
--   mvn --% -Dexec.mainClass=com.workshop.bbs.util.SetupUsers compile exec:java
