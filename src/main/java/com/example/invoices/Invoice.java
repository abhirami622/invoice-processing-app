package com.example.invoices;

import java.time.LocalDate;

public class Invoice {

	private String id;
	private double amount;
	private double paidAmount;
	private LocalDate dueDate;
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isPaid() {
        return paidAmount >= amount;
    }
	
	public boolean isPartiallyPaid() {
        return paidAmount != 0.00 && paidAmount <= amount ;
    }

    public boolean isOverduedate() {
        return LocalDate.now().isAfter(dueDate);
    }

	public Invoice(String id, double amount,  LocalDate dueDate ) {

		this.id = id;
		this.amount = amount;
		this.paidAmount = 0.0;
		this.dueDate = dueDate;
		this.status = "pending";
	}

}
