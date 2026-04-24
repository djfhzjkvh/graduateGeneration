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
