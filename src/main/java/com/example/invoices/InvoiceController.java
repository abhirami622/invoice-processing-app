package com.example.invoices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoices")

public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;

	@PostMapping
	public ResponseEntity<IdCreate> createInvoice(@RequestBody Map<String, Object> details) {
		double amount = (Double) details.get("amount");
		String dueDate = (String) details.get("due_date");
		IdCreate newInvoice = invoiceService.createInvoices(amount, dueDate);
		return new ResponseEntity<>(newInvoice, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<Invoice>> getInvoices() {
		return ResponseEntity.ok(new ArrayList<>(invoiceService.getInvoices()));
	}

	@PostMapping("/{id}/payments")
	public ResponseEntity<Invoice> makePayment(@PathVariable("id") String id,
			@RequestBody Map<String, Double> payment) {
		double paymentAmount = payment.get("amount");
		Invoice updatedInvoice = invoiceService.makePayment(id, paymentAmount);
		return ResponseEntity.ok(updatedInvoice);
	}

	@PostMapping("process-overdue")
	public ResponseEntity<List<Invoice>> overdueInvoices(@RequestBody Map<String, Object> dueDetails) {
		double lateFee = (Double) dueDetails.get("late_fee");
		int overdueDays = (Integer) dueDetails.get("overdue_days");
		List<Invoice> newInvoices = invoiceService.overdueInvoices(lateFee, overdueDays);
		return ResponseEntity.ok(newInvoices);
	}
}