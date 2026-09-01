# Toucan Payments — Transaction Backend

## Project Snapshot

**Total API Endpoints:** 26
**Total Automated Tests:** 80

This project starts from the Toucan Payments Spring Boot starter and implements the
assigned Transaction service, then extends it with an ID Generation module, a
Society/Anvita-inspired ecosystem, and a Payment module.

---

## 1. Problem Understanding

A payment transaction company processes transactions throughout the day. Someone
makes a payment, someone may receive a refund, and every one of those events
needs to be recorded, validated, updated, and retrieved. That is where the
Transaction module comes in — it is the system of record for anything that
moves money.

## 2. Original Assigned Requirement

The assignment asked for a Transaction service built around a Transaction ID,
Customer ID, Amount, Currency, Transaction Type, and Transaction Status, with
exactly four operations:

1. **Create transaction** — validate, store, reject invalid transactions, reject duplicate Transaction IDs.
2. **Get transaction** — retrieve by Transaction ID, respond sensibly if it doesn't exist.
3. **Update transaction status** — change status of an existing transaction, following defined transition rules.
4. **Get customer transactions** — retrieve all transactions for a given Customer ID.

This is the primary, company-assigned work. Everything under Section 4 below is
additional exploration I did on my own, after this part was complete.

## 3. My Approach

I implemented the four required operations first, in `TransactionController` /
`TransactionService`, backed by `TransactionRepository` (Spring Data JPA) and an
explicit status state machine. Once that was working and tested, I asked: what
would a real system around this Transaction module look like — who creates these
transactions, and how? That question led to the additional modules in Section 4.

## 4. Additional Engineering Exploration

**This part was not asked for by the company. I built it voluntarily to see how
the Transaction module could plug into a larger, more realistic payment
ecosystem.**

### ID Generation
Manually typing IDs like Customer ID or Transaction ID isn't realistic for a
real client. `IdGenerator` (`idgeneration` package) builds meaningful,
traceable IDs from the actual entity data supplied (e.g. payer/receiver for a
transaction, resident name/flat for a resident) plus a uniqueness suffix, so
IDs are never purely random and never manually supplied by the caller in the
extended flows.

### Society / Anvita-inspired ecosystem
Using Toucan's Anvita product as a domain reference, I modeled a
gated-community scenario: `Resident`, `Society Admin`, `Property Manager`,
`Security Guard`, and `Merchant` actors, plus `Bill`s (Maintenance, Utility,
One-Time dues) that residents owe. The idea: a society ecosystem generates
bills, bills generate payments, payments generate transactions.

```
Society ecosystem → Bills / dues → Payment → Transaction
```

### Payment Module
The Payment module (`payment` package) connects a payer and receiver to the
Transaction module. It supports four payment methods — UPI, Card, Wallet, Net
Banking — via a Strategy pattern (`PaymentStrategy` + one implementation per
method), and orchestrates order creation, method-specific processing, and the
resulting transaction record.

### Client Flow
`ClientTransactionController` / `ClientTransactionService` provide a
simplified entry point where a client initiates a payment without manually
supplying a Customer ID or Transaction ID — both are generated on the backend
using the ID Generation module.

## 5. Validation Rules

Bean Validation (`jakarta.validation`) is applied on request DTOs across the
Transaction, Payment, and Society modules — for example, required fields
(`@NotBlank`/`@NotNull`), transaction ID length limits, and amount bounds
(`@DecimalMin`/`@DecimalMax`). Business-level validation, like duplicate
Transaction ID detection and status-transition legality, sits in the service
layer rather than as annotations.

Validation matters here because it:
- Prevents invalid data from ever entering the system.
- Stays close to the API boundary, so bad input is rejected before it reaches business logic.
- Returns predictable, structured API errors (`GlobalExceptionHandler`, `PaymentExceptionHandler`, `SocietyExceptionHandler`) instead of raw stack traces.

### Transaction status transitions
```
PENDING   -> COMPLETED, FAILED
FAILED    -> PENDING   (retry)
COMPLETED -> REVERSED  (refund)
REVERSED  -> (terminal — no further transitions)
```
Any transition not listed above is rejected with a 409 Conflict
(`InvalidStatusTransitionException`).

## 6. JPA / Hibernate

Domain entities (`Transaction`, `Bill`, `Bank`, `BankAccount`, `PaymentOrder`,
`PaymentTransaction`, `Resident`, `Merchant`, etc.) are mapped as JPA entities
backed by an H2 in-memory database, using Spring Data JPA repositories for
persistence. Relationships between entities (e.g. a Bill referencing a
Resident, a PaymentOrder referencing BankAccounts) are modeled where the
domain called for it, keeping the persistence layer close to the actual
business shape rather than a flat generic structure.

## 7. API Endpoints

### Assigned Transaction APIs
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/transactions` | Create a transaction |
| GET | `/api/transactions/{id}` | Get a transaction by ID |
| PATCH | `/api/transactions/{id}/status` | Update transaction status |
| GET | `/api/customers/{customerId}/transactions` | Get all transactions for a customer |

### Extended Transaction APIs
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/transactions` | List/filter transactions by status and/or type |
| POST | `/api/transactions/{id}/refund` | Refund a completed transaction |

### Society APIs
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/society/residents` | Register a resident |
| GET | `/api/society/residents/{id}` | Get a resident |
| POST | `/api/society/merchants` | Register a merchant |
| GET | `/api/society/merchants/{id}` | Get a merchant |
| POST | `/api/society/admins` | Register a society admin |
| POST | `/api/society/property-managers` | Register a property manager |
| POST | `/api/society/guards` | Register a security guard |
| POST | `/api/society/bills` | Raise a bill |
| GET | `/api/society/bills/{id}` | Get a bill |
| GET | `/api/society/residents/{residentId}/bills` | Get all bills for a resident |
| POST | `/api/society/bills/{id}/pay` | Pay a bill |
| PATCH | `/api/society/bills/{id}/cancel` | Cancel a bill |
| GET | `/api/society/residents/{residentId}/bills/outstanding` | Get outstanding bills for a resident |
| GET | `/api/society/bills?status=` | Filter bills by status (admin-wide) |
| GET | `/api/society/bills/{id}/receipt` | Get a bill's payment receipt |

### Client Flow APIs
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/clientflow/payments` | Initiate a payment without manually supplying IDs |

### Payment APIs
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/payments` | Create and process a payment (order + strategy + transaction) |
| GET | `/api/payments/{id}` | Get a payment order |
| GET | `/api/payments/{id}/transaction` | Get the transaction(s) resulting from a payment order |

### Other APIs
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/sample` | Starter project's sample endpoint (kept as-is) |

## 8. Testing Approach

The suite uses MockMvc-based controller tests (`@SpringBootTest` +
`@AutoConfigureMockMvc`), service-layer tests, Spring Data JPA repository
tests, and Bean Validation tests, for a total of **80 automated tests**. The
starter project's original sample test (`contextLoads()`) is still present,
alongside the four required Transaction test cases and the additional tests
covering every module built during exploration.

Detailed category-wise test explanations and the complete test execution
output are provided in `TEST-RUN-OUTPUT.txt`.

## 9. Running the Project

```bash
./mvnw clean test
```

On Windows:

```bat
mvnw.cmd clean test
```

## 10. Note on Sample API / Sample Test

The starter project's sample API endpoint (`GET /api/sample`) and its sample
test (`contextLoads()`, inside `TransactionControllerTest`) are still present
in the project; they were not removed.

## 11. AI Usage

AI assistance was used in building and documenting this project. See
`AI-USAGE-DISCLOSURE.md` for details.
