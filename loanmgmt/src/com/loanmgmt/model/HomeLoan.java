package com.loanmgmt.model;

public class HomeLoan extends Loan {
    private String propertyAddress;
    private int propertyValue;

    public HomeLoan() {
    }

    public HomeLoan(int loanId, Customer customer, int principalAmount, int interestRate, int loanTerm, String loanType, String loanStatus, String propertyAddress, int propertyValue) {
        super(loanId, customer, principalAmount, interestRate, loanTerm, loanType, loanStatus);
        this.propertyAddress = propertyAddress;
        this.propertyValue = propertyValue;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public int getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(int propertyValue) {
        this.propertyValue = propertyValue;
    }
}
