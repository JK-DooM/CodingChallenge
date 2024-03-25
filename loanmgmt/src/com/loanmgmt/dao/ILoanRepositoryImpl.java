package com.loanmgmt.dao;

import com.loanmgmt.model.Loan;
import com.loanmgmt.model.Customer;

import com.loanmgmt.exception.InvalidLoanException;
import com.loanmgmt.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ILoanRepositoryImpl implements ILoanRepository {
    private Connection conn;

    public ILoanRepositoryImpl() {
        this.conn = DBUtil.getDBConn();
    }
    @Override
    public void createCustomer(Customer customer) throws SQLException {
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO customer (customer_id, name, email, phone_number, address, credit_score) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(query);
            ps.setInt(1, customer.getCustomerId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getEmail());
            ps.setInt(4, customer.getPhoneNumber());
            ps.setString(5, customer.getAddress());
            ps.setString(6, customer.getCreditScore());

            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    @Override
    public void applyLoan(Loan loan) throws InvalidLoanException {
        try {
            String query = "INSERT INTO loan_table (laon_id, customer_id, principal_amount, interest_rate, loan_term, loan_type, loan_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loan.getLoanId());
            ps.setInt(2, loan.getCustomer().getCustomerId());
            ps.setInt(3, loan.getPrincipalAmount());
            ps.setInt(4, loan.getInterestRate());
            ps.setInt(5, loan.getLoanTerm());
            ps.setString(6, loan.getLoanType());
            ps.setString(7, loan.getLoanStatus());

            System.out.println("Do you want to apply for this loan? (Yes/No)");
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine().trim().toLowerCase();
            
            if (userInput.equals("yes")) {
                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Loan applied successfully!");
                }
            } else {
                System.out.println("Loan application canceled.");
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Failed to apply for loan: " + e.getMessage());
        }
    }

    @Override
    public double calculateInterest(int loanId) throws InvalidLoanException {
        try {
            String query = "SELECT principal_amount, interest_rate, loan_term FROM loan_table WHERE laon_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int principalAmount = rs.getInt("principal_amount");
                int interestRate = rs.getInt("interest_rate");
                int loanTerm = rs.getInt("loan_term");
                return (principalAmount * interestRate * loanTerm) / 12;
            } else {
                throw new InvalidLoanException("Loan not found with ID: " + loanId);
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Failed to calculate interest for loan: " + e.getMessage());
        }
    }

    @Override
    public double calculateInterest(int principalAmount, int interestRate, int loanTerm) {
        return (principalAmount * interestRate * loanTerm) / 12;
    }

    @Override
    public void loanStatus(int loanId) throws InvalidLoanException {
    	 try {
    	        String query = "SELECT credit_score FROM customer WHERE customer_id = (SELECT customer_id FROM loan_table WHERE laon_id = ?)";
    	        PreparedStatement ps = conn.prepareStatement(query);
    	        ps.setInt(1, loanId);
    	        ResultSet rs = ps.executeQuery();

    	        if (rs.next()) {
    	            int creditScore = Integer.parseInt(rs.getString("credit_score"));
    	            if (creditScore > 650) {
    	                System.out.println("Loan approved!");
    	                String updateQuery = "UPDATE loan_table SET loan_status = 'Approved' WHERE laon_id = ?";
    	                PreparedStatement updatePs = conn.prepareStatement(updateQuery);
    	                updatePs.setInt(1, loanId);
    	                int rowsUpdated = updatePs.executeUpdate();
    	                if (rowsUpdated > 0) {
    	                    System.out.println("Loan status updated to Approved.");
    	                }
    	            } else {
    	                System.out.println("Loan rejected!");
    	                String updateQuery = "UPDATE loan_table SET loan_status = 'Rejected' WHERE laon_id = ?";
    	                PreparedStatement updatePs = conn.prepareStatement(updateQuery);
    	                updatePs.setInt(1, loanId);
    	                int rowsUpdated = updatePs.executeUpdate();
    	                if (rowsUpdated > 0) {
    	                    System.out.println("Loan status updated to Rejected.");
    	                }
    	            }
    	        } else {
    	            throw new InvalidLoanException("Loan not found with ID: " + loanId);
    	        }
    	    } catch (SQLException e) {
    	        throw new InvalidLoanException("Failed to check loan status: " + e.getMessage());
    	    }	
    }

    @Override
    public double calculateEMI(int loanId) throws InvalidLoanException {
        try {
            String query = "SELECT principal_amount, interest_rate, loan_term FROM loan_table WHERE laon_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int principalAmount = rs.getInt("principal_amount");
                int interestRate = rs.getInt("interest_rate");
                int loanTerm = rs.getInt("loan_term");
                double monthlyInterestRate = (interestRate / 12.0) / 100.0;
                int totalMonths = loanTerm * 12;
                return (principalAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, totalMonths)) / (Math.pow(1 + monthlyInterestRate, totalMonths) - 1);
            } else {
                throw new InvalidLoanException("Loan not found with ID: " + loanId);
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Failed to calculate EMI for loan: " + e.getMessage());
        }
    }

    @Override
    public double calculateEMI(int principalAmount, int interestRate, int loanTerm) {
        double monthlyInterestRate = (interestRate / 12.0) / 100.0;
        int totalMonths = loanTerm * 12;
        return (principalAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, totalMonths)) / (Math.pow(1 + monthlyInterestRate, totalMonths) - 1);
    }

    @Override
    public void loanRepayment(int loanId, double amount) {
        try {
            String query = "SELECT principal_amount, interest_rate, loan_term, loan_status FROM loan_table WHERE laon_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int principalAmount = rs.getInt("principal_amount");
                int interestRate = rs.getInt("interest_rate");
                int loanTerm = rs.getInt("loan_term");
                String loanStatus = rs.getString("loan_status");
                double emi = calculateEMI(principalAmount, interestRate, loanTerm);
                int noOfEMIs = (int) Math.ceil(amount / emi);

                if (noOfEMIs > 0) {
                    System.out.println("Number of EMIs that can be paid: " + noOfEMIs);

                    if (noOfEMIs * emi >= principalAmount) {
                        String updateQuery = "UPDATE loan_table SET loan_status = 'Paid' WHERE laon_id = ?";
                        PreparedStatement updatePs = conn.prepareStatement(updateQuery);
                        updatePs.setInt(1, loanId);
                        int rowsUpdated = updatePs.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Loan status updated to Paid.");
                        }
                    } else {
                        System.out.println("EMIs paid. Loan status remains: " + loanStatus);
                    }
                } else {
                    System.out.println("Amount is less than a single EMI. Repayment rejected.");
                }
            } else {
                throw new SQLException("Loan not found with ID: " + loanId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void getAllLoan() {
        try {
            String query = "SELECT * FROM loan_table";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	System.out.println("Loan Details:");
                System.out.println("Loan ID: " + rs.getInt("laon_id"));
                System.out.println("Customer ID: " + rs.getInt("customer_id"));
                System.out.println("Principal Amount: " + rs.getInt("principal_amount"));
                System.out.println("Interest Rate: " + rs.getInt("interest_rate"));
                System.out.println("Loan Term: " + rs.getInt("loan_term"));
                System.out.println("Loan Type: " + rs.getString("loan_type"));
                System.out.println("Loan Status: " + rs.getString("loan_status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getLoanById(int loanId) throws InvalidLoanException {
        try {
            String query = "SELECT * FROM loan_table WHERE laon_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Loan Details:");
                System.out.println("Loan ID: " + rs.getInt("laon_id"));
                System.out.println("Customer ID: " + rs.getInt("customer_id"));
                System.out.println("Principal Amount: " + rs.getInt("principal_amount"));
                System.out.println("Interest Rate: " + rs.getInt("interest_rate"));
                System.out.println("Loan Term: " + rs.getInt("loan_term"));
                System.out.println("Loan Type: " + rs.getString("loan_type"));
                System.out.println("Loan Status: " + rs.getString("loan_status"));
            } else {
                throw new InvalidLoanException("Loan not found with ID: " + loanId);
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Failed to get loan details: " + e.getMessage());
        }
    }

}
