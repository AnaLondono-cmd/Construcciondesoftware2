# nexusmarket-service

NexusMarket domain and application services, implemented with **Spring Boot 3 / Java 17**,
integrated with the domain model from the first deliverable (`domain-model-nexusmarket.md`).

This module resolves, through services, the use cases described in the *Business Functional
Specification* and enforces its business rules (BR-01 to BR-03 and the Critical Validations
in section 11).

## 1. Architecture

Layered architecture (a lightweight hexagonal/layered style), with no business logic in the web
layer (no REST is exposed in this deliverable; the focus is the domain/application services, as
requested):

```
com.nexusmarket
├── domain/              JPA entities implementing the domain model (aggregate roots and value objects)
│   └── enums/           Closed catalogs (UserRole, OrderStatus, MovementType, etc.)
├── repository/           Spring Data JPA — persistence access, no business logic
├── service/              Service interfaces — one per bounded use-case area (abstractions)
│   └── impl/             Concrete implementations (@Service), one per interface
├── exception/             Business exceptions (one per violated rule, never generic)
└── config/                Configuration and the end-to-end business-flow demo runner
```

**Coding rules applied:**
- Every domain class protects its own invariants (e.g. `Inventory.reserve()` validates
  availability before mutating its state; `Order` rejects transitions outside its lifecycle).
- **Program to abstractions:** every service is defined first as an interface
  (`OrderService`, `InventoryService`, ...) and consumed by other services through that interface;
  `impl` classes are the only place a concrete implementation is visible, and Spring wires them by
  constructor injection (Dependency Inversion Principle).
- Services are the single entry point to every use case: no setter for critical state is exposed
  without going through a business method.
- Constructor-based dependency injection everywhere (no field `@Autowired`).
- One specific business exception per rule (`InsufficientStockException`,
  `OrderNotModifiableException`, `DuplicateRegistrationException`, `UnauthorizedRoleException`),
  all extending `NexusMarketException`, enabling differentiated handling in upper layers.
- Persistence via Spring Data JPA over an in-memory H2 database (an implementation decision for
  this deliverable; the domain model itself stays storage-agnostic, as required by section 3.2 of
  the specification).


## 2. Domain → Service → Use case mapping

| Service (interface / impl) | Functional domain (spec) | Use cases resolved |
|---|---|---|
| `UserService` / `UserServiceImpl` | User & Buyer Management | Buyer self-registration, user blocking/activation |
| `AdministratorService` / `AdministratorServiceImpl` | Seller & Warehouse Management | Seller onboarding and warehouse registration (a seller never self-registers) |
| `ProductService` / `ProductServiceImpl` | Catalog Management | Product/variant creation, publish/suspend/discontinue |
| `InventoryService` / `InventoryServiceImpl` | Inventory Management | Inbound, reservation, release, sale outbound, adjustment and return of stock |
| `CartService` / `CartServiceImpl` | Shopping cart | Provisional product selection |
| `OrderService` / `OrderServiceImpl` | Order Management | Order confirmation, payment, shipping and completion — orchestrates Cart/Inventory/Invoice |
| `InvoicingService` / `InvoicingServiceImpl` | Invoicing | Invoice issuance and voiding |
| `LogisticsService` / `LogisticsServiceImpl` | Shipment Management | Dispatch, tracking and delivery confirmation |
| `ReturnService` / `ReturnServiceImpl`, `RefundService` / `RefundServiceImpl` | Returns & Refunds | Return request/approval and refund generation/processing |
| `ReportService` / `ReportServiceImpl` | Administrative reporting | Read-only queries for the Supervisor role |

## 3. Business rules implemented (traceability)

| Rule | Description | Where it is enforced |
|---|---|---|
| BR-01 | Every operation is executed by an authenticated user | Every service method receives the id of the acting user (e.g. `buyerId`, `sellerId`) |
| BR-02 | One user, one single role | `User` inheritance hierarchy (`JOINED` strategy); every concrete subclass fixes its own `UserRole` in its constructor |
| BR-03 | No one manages information outside their own role | Services expose only the operations of their own domain (e.g. `CartService` never exposes direct inventory reservations) |
| Inventory never negative | `Inventory.adjust()`, `.reserve()`, `.confirmOutbound()` throw `InsufficientStockException` instead of allowing negative quantities |
| No reservation on damaged stock | `Inventory.checkAvailability()` checks `markedAsDamaged` before reserving |
| Seller cannot self-register | Single creation point: `AdministratorService.registerSeller()` |
| Unique email/identifier | `UserRepository.existsByEmail()` checked before any registration |
| Completed order is immutable | `Order.isModifiable()` blocks `confirmPayment()`, `markAsShipped()`, `cancel()` once `DELIVERED_COMPLETED` or `CANCELLED` |

## 4. How to run

Requires JDK 17 and Maven.

```bash
# Compile and run the business-rule unit tests
mvn test

# Start the application (service layer + H2 console at /h2-console)
mvn spring-boot:run

# Run the demo that walks through the full business flow (specification section 6.1)
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

The demo (`BusinessFlowDemoRunner`) executes, using only the application services: seller
onboarding → product registration → initial inventory → publication → cart → order →
payment/invoice → dispatch/shipment → delivery/completion → return → refund.

## 5. Included tests

`BusinessRulesTest` validates, at the unit level and without a database, the most sensitive
domain invariants: non-negative inventory, blocked reservations on damaged stock, and the
immutability of a completed/cancelled order.

## 6. Left for future iterations

- A REST API (`@RestController`) exposed on top of these services, with centralized exception
  handling (`@ControllerAdvice`) mapping each `NexusMarketException` to an HTTP status code.
- Security and technical authentication (out of scope for the functional specification, section 3.2).
- Migration from H2 to a production-grade database (PostgreSQL/MySQL) via Flyway/Liquibase.
