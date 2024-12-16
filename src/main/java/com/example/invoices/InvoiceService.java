package com.example.invoices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
	

	private static final Map<String, Invoice> invoiceMap = new HashMap<>();



	public IdCreate createInvoices(double amount, String dueDate) {

		String id = UUID.randomUUID().toString();
		LocalDate due = LocalDate.parse(dueDate);
		Invoice invoice = new Invoice(id, amount, due);
		invoiceMap.put(id, invoice);
		IdCreate idCreate = new IdCreate();
		idCreate.setId(id);
		return idCreate;
	}

	public Collection<Invoice> getInvoices() {
		return invoiceMap.values();
	}
	
	public Invoice makePayment(String id, double paymentAmount) {
		Invoice invoice = invoiceMap.get(id);
		if (invoice != null && invoice.getStatus().equals("pending")) {
			double newPaidAmount = invoice.getPaidAmount() + paymentAmount;
			invoice.setPaidAmount(newPaidAmount);
			if (invoice.isPaid()) {
				invoice.setStatus("paid");
			}
		}
		return invoice;
	}

	public List<Invoice> overdueInvoices(double lateFee, int overdueDays) {

		List<Invoice> newInvoices = new ArrayList<>();
		LocalDate today = LocalDate.now();

		for (Invoice invoice : invoiceMap.values()) {
			if (invoice.getStatus().equals("pending") && invoice.isOverduedate()) {

				double remainingAmount = invoice.getAmount() - invoice.getPaidAmount();
				double lateAmount = lateFee + remainingAmount;

				if (invoice.isPartiallyPaid()) {
					invoice.setStatus("paid");
					newInvoiceCreated(overdueDays, newInvoices, today, lateAmount);

				} else {
					invoice.setStatus("void");
					newInvoiceCreated(overdueDays, newInvoices, today, lateAmount);
				}
			}
		}
		return newInvoices;
	}

	public void newInvoiceCreated(int overdueDays, List<Invoice> newInvoices, LocalDate today, double lateAmount) {
		Invoice newInvoice = new Invoice(UUID.randomUUID().toString(), lateAmount, today.plusDays(overdueDays));
		newInvoices.add(newInvoice);
		invoiceMap.put(newInvoice.getId(), newInvoice);
	}
}