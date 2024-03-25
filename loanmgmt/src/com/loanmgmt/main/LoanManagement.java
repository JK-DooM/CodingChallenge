package com.loanmgmt.main;

import java.util.Scanner;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.loanmgmt.util.*;
import com.loanmgmt.dao.*;
import com.loanmgmt.exception.*;
import com.loanmgmt.model.*;

public class LoanManagement {
    private static final ILoanRepository lr = new ILoanRepositoryImpl();
    Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        boolean exit = false;
        Scanner in = new Scanner(System.in);

        while (!exit) {
            System.out.println("Loan Management System Menu:");
            System.out.println("1. Create Customer");
            System.out.println("2. Apply for a Loan");
            System.out.println("3. Get All Loans");
            System.out.println("4. Get Loan by ID");
            System.out.println("5. Loan Repayment");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = in.nextInt();
            in.nextLine(); 
            
            switch (choice) {
            // added create customer, it was not available in pdf.
            case 1:
                createCustomer();
                break;
            case 2:
                applyLoan();
                break;
            case 3:
                getAllLoans();
                break;
            case 4:
                getLoanById();
                break;
            case 5:
                loanRepayment();
                break;
            case 6:
                exit = true;
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
}
    private static void createCustomer() {
        Customer customer = createCustomerDetails();
        try {
            lr.createCustomer(customer);
            System.out.println("Customer created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
    }

    private static Customer createCustomerDetails() {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter customer details:");

        System.out.print("Customer ID: ");
        int customerId = in.nextInt();
        in.nextLine();

        System.out.print("Name: ");
        String name = in.nextLine().trim();

        System.out.print("Email Address: ");
        String email = in.nextLine().trim();

        System.out.print("Phone Number: ");
        int phoneNumber = in.nextInt();
        in.nextLine(); 

        System.out.print("Address: ");
        String address = in.nextLine().trim();

        System.out.print("Credit Score: ");
        String creditScore = in.nextLine().trim();

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        customer.setCreditScore(creditScore);

        return customer;
    }

    //apply loan only for existing customer
    private static void applyLoan() {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter loan details:");

        System.out.print("Loan ID: ");
        int loanId = in.nextInt();
        in.nextLine(); 

        System.out.print("Customer ID: ");
        int customerId = in.nextInt();
        in.nextLine();
        
        // since we created the customer details , now we can check whthr the customer exists
        boolean customerExists = checkCustomerExists(customerId);
        if (!customerExists) {
            System.out.println("Error: Customer ID does not exist.");
            return;
        }
        System.out.print("Principal Amount: ");
        int principalAmount = in.nextInt();
        in.nextLine();

        System.out.print("Interest Rate: ");
        int interestRate = in.nextInt();
        in.nextLine();

        System.out.print("Loan Term (in months): ");
        int loanTerm = in.nextInt();
        in.nextLine();
        System.out.print("Loan Type (CarLoan, HomeLoan): ");
        String loanType = in.nextLine().trim();

        String loanStatus = "Pending";

        Loan loan = new Loan();
        loan.setLoanId(loanId);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        loan.setCustomer(customer);

        loan.setPrincipalAmount(principalAmount);
        loan.setInterestRate(interestRate);
        loan.setLoanTerm(loanTerm);
        loan.setLoanType(loanType);
        loan.setLoanStatus(loanStatus);

        try {
            lr.applyLoan(loan);
        } catch (InvalidLoanException e) {
            System.out.println("Error applying for loan: " + e.getMessage());
        }
    }
    private static boolean checkCustomerExists(int customerId) {
        boolean customerExists = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan","root","LuffyZoroSanji1!");

            String query = "SELECT COUNT(*) FROM customer WHERE customer_id = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);

            rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    customerExists = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking if customer exists: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return customerExists;
    }


    private static void getAllLoans() {
        lr.getAllLoan();
    }

    private static void getLoanById() {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter loan ID: ");
        int loanId = in.nextInt();
        in.nextLine();
        try {
            lr.getLoanById(loanId);
        } catch (InvalidLoanException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void loanRepayment() {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter loan repayment details:");
        System.out.print("Loan ID: ");
        int loanId = in.nextInt();
        in.nextLine();
        System.out.print("Amount to be repaid: ");
        double amount = in.nextDouble();
        in.nextLine();
        
		lr.loanRepayment(loanId, amount);
    }
}
