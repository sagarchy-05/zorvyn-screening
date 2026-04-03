# Finance Dashboard Backend

A backend system for managing financial records, user roles, and dashboard analytics — built with Spring Boot and PostgreSQL.

---

## Tech Stack

- **Java 21** with **Spring Boot 3.3.2**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **PostgreSQL 16** for persistence
- **Docker & Docker Compose** for containerization
- **Springdoc OpenAPI** for interactive API documentation
- **Lombok** to reduce boilerplate

---

## Getting Started

### Prerequisites

- Docker installed

### Run the Project

```bash
# 1. Clone the repository
git clone https://github.com/your-username/finance-dashboard.git
cd finance-dashboard

# 2. Create your environment file
cp .env.example .env

# 3. Start everything
docker compose up --build
```

That's it. Postgres starts first (with a health check), Hibernate creates the tables, and the seeder populates initial data.

### Access Points

| Resource       | URL                                          |
|----------------|----------------------------------------------|
| API Base       | `http://localhost:8080/api/v1`               |
| Swagger UI     | `http://localhost:8080/api/v1/swagger-ui.html`|
| API Docs (JSON)| `http://localhost:8080/api/v1/api-docs`      |

### Seeded Users

The app seeds three users on first startup — one per role:

| Username  | Password     | Role    |
|-----------|-------------|---------|
| admin     | admin123    | ADMIN   |
| analyst   | analyst123  | ANALYST |
| viewer    | viewer123   | VIEWER  |

18 financial records across three months are also seeded so the dashboard returns meaningful data immediately.

---

## API Reference

### Authentication (Public)

| Method | Endpoint             | Description                                   |
|--------|----------------------|-----------------------------------------------|
| POST   | `/api/v1/auth/register` | Register a new viewer & login simultaneouslty |
| POST   | `/api/v1/auth/login`    | Login, receive JWT                            |

**Register:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login:**
```json
{
  "username": "john",
  "password": "secret123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzM4...",
  "username": "john",
  "role": "VIEWER"
}
```

Use the token in subsequent requests:
```
Authorization: Bearer eyJhbGciOiJIUzM4...
```

### Financial Records

| Method | Endpoint            | Access  | Description              |
|--------|---------------------|---------|--------------------------|
| GET    | `/api/v1/records`      | VIEWER+ | List records (filtered)  |
| GET    | `/api/v1/records/{id}` | VIEWER+ | Get a single record      |
| POST   | `/api/v1/records`      | ADMIN   | Create a record          |
| PUT    | `/api/v1/records/{id}` | ADMIN   | Update a record          |
| DELETE | `/api/v1/records/{id}` | ADMIN   | Soft-delete a record     |

**Filter parameters** for `GET /api/v1/records`:

| Param       | Type       | Example           |
|-------------|------------|-------------------|
| `type`      | Enum       | `INCOME`, `EXPENSE` |
| `category`  | String     | `Salary`          |
| `startDate` | Date       | `2025-01-01`      |
| `endDate`   | Date       | `2025-12-31`      |
| `page`      | Integer    | `0`               |
| `size`      | Integer    | `20`              |
| `sort`      | String     | `recordDate,desc` |

**Pagination:**
- Default page size: `10`
- Maximum page size: `20`

### 📅 Date Format

All date parameters (`startDate`, `endDate`) must follow ISO-8601 format: yyyy-MM-dd

Example:
GET /api/v1/records?startDate=2025-01-01&endDate=2025-12-31

Invalid formats (will return 400):
- 01-01-2025
- 2025/01/01
- Jan 1, 2025

**Create record:**
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-04-01",
  "description": "Monthly salary"
}
```

### Dashboard (ANALYST and ADMIN)

| Method | Endpoint                 | Description             |
|--------|--------------------------|-------------------------|
| GET    | `/api/v1/dashboard/summary` | Full dashboard overview |

**Response:**
```json
{
  "totalIncome": 22800.00,
  "totalExpenses": 5125.00,
  "netBalance": 17675.00,
  "totalRecords": 18,
  "categoryTotals": [
    { "category": "Salary", "type": "INCOME", "total": 15000.00 },
    { "category": "Rent", "type": "EXPENSE", "total": 3600.00 }
  ],
  "monthlyTrends": [
    { "year": 2025, "month": 4, "income": 5800.00, "expenses": 1660.00, "net": 4140.00 }
  ],
  "recentActivity": []
}
```

### User Management (ADMIN only)

| Method | Endpoint          | Description                     |
|--------|-------------------|---------------------------------|
| GET    | `/api/v1/users`      | List all users (paginated)      |
| GET    | `/api/v1/users/{id}` | Get user details                |
| PUT    | `/api/v1/users/{id}` | Update role or status           |

---

## Role-Based Access Control

Access is enforced at two levels — URL pattern matching in the security filter chain and `@PreAuthorize` annotations on controllers.

| Action                      | VIEWER | ANALYST | ADMIN |
|-----------------------------|--------|---------|-------|
| View records                | Yes    | Yes     | Yes   |
| Access dashboard summary    | No     | Yes     | Yes   |
| Create / update / delete records | No | No     | Yes   |
| Manage users                | No     | No      | Yes   |

---

## Project Structure

```
src/main/java/com/financedashboard/
├── config/          Security chain, JWT filter, OpenAPI config
├── controller/      REST endpoints (thin, delegates to services)
├── dto/             Request and response objects
├── entity/          JPA entities and enums
├── exception/       Custom exceptions and global error handler
├── mapper/          Entity ↔ DTO conversion
├── repository/      Spring Data JPA interfaces with custom queries
├── seed/            Startup data seeder
└── service/         All business logic
```

---

## Best Practices Followed

### Architecture

- **API versioning via context path** — the `/api/v1` prefix is set once in `application.yml` using `server.servlet.context-path`, keeping controller mappings clean and making future version bumps a single-line change.
- **Layered architecture** with clear separation — controllers handle HTTP, services handle logic, repositories handle data. No layer skips another.
- **DTO pattern** — entities are never exposed to the API. Dedicated request and response objects keep the internal model decoupled from the API contract.
- **Mapper classes** — static utility classes handle entity-to-DTO conversion, keeping the mapping logic centralized and testable.

### Security

- **Stateless JWT authentication** — no server-side sessions. Each request carries its own auth context via the `Authorization` header.
- **BCrypt password hashing** — passwords are never stored in plain text.
- **Two-level access control** — URL-based rules in `SecurityConfig` provide broad protection, while `@PreAuthorize` on controllers adds fine-grained method-level checks.
- **No secrets in source code** — all credentials and keys are injected via environment variables. The `.env` file is git-ignored and a `.env.example` template is provided.

### Data

- **`BigDecimal` for monetary values** — avoids the floating-point precision issues that come with `double` or `float`.
- **Soft delete** — records are never physically removed. A `deleted` flag preserves data integrity and audit trails. All queries filter on `deleted = false`.
- **Partial updates** — `UpdateRecordRequest` fields are all optional. Only non-null fields overwrite the entity, preventing accidental data loss.
- **Pagination on all list endpoints** — responses use Spring's `Page` object with metadata like `totalElements` and `totalPages`, preventing unbounded result sets.

### Error Handling

- **Global exception handler** (`@RestControllerAdvice`) catches all exceptions in one place and returns consistent `ApiErrorResponse` objects.
- **Field-level validation errors** — when input validation fails, the response includes a `fieldErrors` map showing exactly which fields failed and why.
- **Proper HTTP status codes** — `201` for creation, `204` for deletion, `400` for validation, `401` for auth failure, `403` for forbidden, `404` for not found, `409` for duplicates.
- **No internal leaks** — `server.error.include-message: never` ensures stack traces and internal details are not exposed to clients.

### Docker

- **Multi-stage Dockerfile** — Stage 1 builds with Maven (full JDK), Stage 2 runs with only the JRE. This keeps the final image small.
- **Dependency caching** — `pom.xml` is copied and resolved before the source code, so Docker caches the dependency layer. Rebuilds are fast unless dependencies change.
- **Health check on Postgres** — the app container waits for Postgres to actually accept connections (not just for the container to start) before booting.
- **Named volume for persistence** — database data survives `docker-compose down` and container restarts.
- **Three-link environment chain** — `.env` → `docker-compose.yml` → `application.yml`. Secrets live in one place, flow through cleanly, and never touch source control.

### Code Quality

- **Lombok** — reduces boilerplate for getters, setters, builders, constructors, and logging without sacrificing readability.
- **Hibernate `ddl-auto: update`** — schema is derived directly from entity annotations, keeping a single source of truth for the data model.
- **Idempotent seeder** — the `DataSeeder` checks for existing data before inserting, so restarting the app doesn't create duplicates.
- **`@Transactional(readOnly = true)`** on read operations — hints to Hibernate to skip dirty checking, improving query performance.

---

## Assumptions

1. The seeded admin credentials are for development only and should be changed in any deployed environment.
2. `ddl-auto: update` is used for simplicity. A production system would use a migration tool like Flyway or Liquibase.
3. JWT tokens have a 24-hour expiry with no refresh token mechanism.
4. The soft-delete implementation uses a boolean flag. A production system might add a `deletedAt` timestamp and a `deletedBy` reference.

---

## Stopping the Application

```bash
# Stop containers (keep data)
docker-compose down

# Stop containers and wipe database
docker-compose down -v
```