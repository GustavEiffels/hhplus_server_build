CREATE TABLE departments (
    dept_id INT PRIMARY KEY,      
    dept_name VARCHAR(255)        
);

CREATE TABLE employees (
    emp_id INT PRIMARY KEY,         -- 직원 ID (Primary Key)
    name VARCHAR(255),              -- 직원 이름
    gender VARCHAR(10),             -- 성별
    age INT,                        -- 나이
    birthdate DATE,                 -- 생년월일
    major VARCHAR(255),             -- 전공
    city VARCHAR(255),              -- 도시
    salary INT,                     -- 연봉
    wealth INT,                     -- 자산
    dept_id INT,                    -- 부서 ID (Foreign Key)
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)  -- 외래 키 제약 조건
);
