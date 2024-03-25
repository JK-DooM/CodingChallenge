package com.loanmgmt.dao;

import com.loanmgmt.model.Customer;
import com.loanmgmt.model.Loan;

import java.sql.SQLException;

import com.loanmgmt.exception.InvalidLoanException;

public interface ILoanRepository {
    void applyLoan(Loan loan) throws InvalidLoanException;
    double calculateInterest(int loanId) throws InvalidLoanException;
    double calculateInterest(int principalAmount, int interestRate, int loanTerm);
    void loanStatus(int loanId) throws InvalidLoanException;
    double calculateEMI(int loanId) throws InvalidLoanException;
    double calculateEMI(int principalAmount, int interestRate, int loanTerm);
    void loanRepayment(int loanId, double amount);
    void getAllLoan();
    void getLoanById(int loanId) throws InvalidLoanException;
	void createCustomer(Customer customer) throws SQLException;
}
