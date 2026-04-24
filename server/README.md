# graduate-generation-server

Spring Boot backend for the customer purchase intention follow-up reminder system.

## Run

Set database credentials if needed:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="20030401"
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Default database:

```text
graduate_generation
```

## MVP APIs

- `POST /api/app/auth/login`
- `GET /api/app/auth/profile`
- `POST /api/app/auth/logout`
- `POST /api/admin/auth/login`
- `GET /api/admin/auth/profile`
- `POST /api/admin/auth/logout`
- `GET /api/app/customers`
- `GET /api/app/customers/{id}`
- `POST /api/app/customers`
- `PUT /api/app/customers/{id}`
- `DELETE /api/app/customers/{id}`
- `GET /api/app/customers/{customerId}/follows`
- `POST /api/app/follows`
- `GET /api/app/tasks`
- `POST /api/app/tasks`
- `PUT /api/app/tasks/{id}/complete`
- `GET /api/admin/customers`
- `GET /api/admin/tasks`
- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`
- `GET /api/admin/roles`
- `GET /api/admin/depts/tree`

## Demo accounts

```text
admin / 123456
manager_hx / 123456
manager_nk / 123456
advisor_a / 123456
advisor_b / 123456
advisor_c / 123456
```

## Swagger

```text
http://localhost:8080/swagger-ui.html
```
