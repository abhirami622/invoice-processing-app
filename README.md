# Invoice Processing System

This invoice system allows creating invoices, paying invoices, and processing overdue invoices.

## API Endpoints

### Invoices

- **GET /invoices**
  - Description: Retrieve a list of all invoices.
  - Example:
   GET http://localhost:8080/invoices

- **POST /invoices**
  - Description: Create a new invoice.
  - Request Body:
    ```json
    {
    "amount": 199.99,
    "due_date": "2021-09-11"
    }
    ```
  - Example:
     POST http://localhost:8080/invoices 
    {
    "amount": 199.99,
    "due_date": "2021-09-11"
     }

    **POST /invoices/{id}/payments**
  - Description: Checks if the invoice is fully paid and the invoice is marked as paid.
  - Request Body:
    ```json
    {
    "amount": 159.99
    }
    ```
  - Example:
     POST http://localhost:8080/invoices/ae2319aa-b221-4286-a082-7ac9b3306d49/payments
    {
    "amount": 159.99
    }

      **POST /invoices/process-overdue**
  - Description: Should process all pending invoices that are overdue. Scenarios with status as pending or void, a new invoice would be created with the late amount and the new overdue date.
  - Request Body:
    ```json
    {
     "late_fee": 10.5,
    "overdue_days": 10
    }
    ```
  - Example:
     POST http://localhost:8080/invoices/process-overdue
    {
    "late_fee": 10.5,
    "overdue_days": 10
   }
    
