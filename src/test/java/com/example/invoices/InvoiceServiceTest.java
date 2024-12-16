package com.example.invoices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InvoiceServiceTest {

	private Invoice mockedInvoice;
	private Invoice newInvoice;

	@Mock
	private Map<String, Invoice> invoiceMap;

	@InjectMocks
	private InvoiceService invoiceService;

	@Mock
	private List<Invoice> newInvoices;

	@BeforeEach
	void setUp() {

		mockedInvoice = mock(Invoice.class);

		newInvoice = mock(Invoice.class);

		Invoice invoice1 = new Invoice("1", 100.0, LocalDate.now().plusDays(5));
		Invoice invoice2 = new Invoice("2", 200.0, LocalDate.now().plusDays(10));
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateInvoices() {
		double amount = 100.0;
		String dueDate = "2024-12-30";

		when(invoiceMap.put(anyString(), any(Invoice.class))).thenReturn(null);

		IdCreate idCreate = invoiceService.createInvoices(amount, dueDate);

		assertNotNull(idCreate.getId());
		String generatedId = idCreate.getId();

		verify(invoiceMap).put(eq(generatedId), any(Invoice.class));

		Invoice createdInvoice = invoiceMap.get(generatedId);
		assertNotNull(createdInvoice);
		assertEquals(amount, createdInvoice.getAmount());
		assertEquals(LocalDate.parse(dueDate), createdInvoice.getDueDate());
	}

	@Test
	public void testGetInvoices() {
		Collection<Invoice> invoices = invoiceService.getInvoices();
		assertNotNull(invoices, "The invoices collection should not be null.");
		assertEquals(2, invoices.size(), "There should be 2 invoices in the collection.");

		assertTrue(invoices.stream().anyMatch(invoice -> invoice.getId().equals("invoice1")),
				"Invoice 1 should be present.");
		assertTrue(invoices.stream().anyMatch(invoice -> invoice.getId().equals("invoice2")),
				"Invoice 2 should be present.");
	}

	@Test
	public void testGetInvoices_Empty() {
		when(invoiceMap.values()).thenReturn(Collections.emptyList());
		Collection<Invoice> invoices = invoiceService.getInvoices();
		assertNotNull(invoices, "The invoices collection should not be null.");
		assertTrue(invoices.isEmpty(), "The invoices collection should be empty.");
	}

	@Test
	public void testMakePayment_ValidInvoice_PendingStatus() {
		Invoice result = invoiceService.makePayment("1", 50.0);
		assertNotNull(result, "The invoice should be returned.");
		assertEquals(50.0, result.getPaidAmount(), "The paid amount should be updated correctly.");
		assertEquals("pending", result.getStatus(), "The status should remain pending.");
	}

	@Test
	public void testMakePaymentFullyPaidInvoice() {
		Invoice result = invoiceService.makePayment("1", 50.0);
		assertNotNull(result, "The invoice should be returned.");
		assertEquals(100.0, result.getPaidAmount(), "The paid amount should not change.");
		assertEquals("paid", result.getStatus(), "The invoice should remain paid.");
	}

	@Test
	public void testOverdueInvoices() {
		double lateFee = 10.0;
		int overdueDays = 5;

		List<Invoice> result = invoiceService.overdueInvoices(lateFee, overdueDays);

		assertNotNull(result, "The result should not be null.");
		assertEquals(2, result.size(), "Two new invoices should be created.");

		assertEquals(110.0, result.get(0).getAmount(),
				"The new invoice should include the late fee and remaining amount.");
		assertEquals(160.0, result.get(1).getAmount(),
				"The new invoice should include the late fee and remaining amount.");

		verify(invoiceMap, times(2)).put(anyString(), any(Invoice.class));
	}

	@Test
	public void testOverdueInvoices_OnlyOverdueInvoicesProcessed() {
		Invoice paidInvoice = new Invoice("3", 200.0, LocalDate.now().plusDays(10));
		paidInvoice.setPaidAmount(200.0);
		Invoice invoice1 = new Invoice("1", 100.0, LocalDate.now().plusDays(5));
		Invoice invoice2 = new Invoice("2", 200.0, LocalDate.now().plusDays(10));
		when(invoiceMap.values()).thenReturn(List.of(invoice1, invoice2, paidInvoice));
		double lateFee = 20.0;
		int overdueDays = 10;
		List<Invoice> result = invoiceService.overdueInvoices(lateFee, overdueDays);
		assertNotNull(result, "The result should not be null.");
		assertEquals(2, result.size(), "Only 2 invoices should be processed (not the paid one).");
		assertEquals(200.0, paidInvoice.getAmount(), "The paid invoice should not be modified.");
	}

	@Test
	public void testNewInvoiceCreated() {
		int overdueDays = 5;
		LocalDate today = LocalDate.now();
		double lateAmount = 100.0;

		invoiceService.newInvoiceCreated(overdueDays, newInvoices, today, lateAmount);
		verify(newInvoices, times(1)).add(any(Invoice.class));
		verify(invoiceMap, times(1)).put(anyString(), any(Invoice.class));
		Invoice createdInvoice = newInvoices.get(0);
		assertEquals(lateAmount, createdInvoice.getAmount(),
				"Invoice amount should match the late fee + remaining amount.");
		assertEquals(today.plusDays(overdueDays), createdInvoice.getDueDate(),
				"The due date should be today's date + overdue days.");
		assertNotNull(createdInvoice.getId(), "Invoice ID should be generated and not null.");
	}
}