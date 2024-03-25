package com.loanmgmt.model;

public class Loan {
    private int loanId;
    private Customer customer;
    private int principalAmount;
    private int interestRate;
    private int loanTerm;
    private String loanType;
    private String loanStatus;

    public Loan() {
    }

    public Loan(int loanId, Customer customer, int principalAmount, int interestRate, int loanTerm, String loanType, String loanStatus) {
        this.loanId = loanId;
        this.customer = customer;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
        this.loanType = loanType;
        this.loanStatus = loanStatus;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(int principalAmount) {
        this.principalAmount = principalAmount;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }
    public void printLoanInfo() {
        System.out.println("Loan ID: " + loanId);
        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Principal Amount: " + principalAmount);
        System.out.println("Interest Rate: " + interestRate);
        System.out.println("Loan Term: " + loanTerm);
        System.out.println("Loan Type: " + loanType);
        System.out.println("Loan Status: " + loanStatus);
    }
}
