	create database loan;
    
    use loan;
    
    CREATE TABLE customer(
    customer_id INT PRIMARY KEY,
    name VARCHAR(25),
    email VARCHAR(30),
    phone_number int,
    address varchar(100),
    credit_score varchar(10)
);
CREATE TABLE loan_table (
    laon_id INT PRIMARY KEY,
    customer_id INT,
	principal_amount int,
    interest_rate int,
    loan_term int,
    loan_type varchar(20),
    loan_status varchar(20)
);
select * from customer;
select * from loan_table;