# graduate-generation-server

Spring Boot backend for the customer purchase intention follow-up reminder system.

## Run

Set database credentials if needed:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="20030401"
$env:QWEN_API_KEY="your-api-key"
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
- `GET /api/app/dashboard/workbench?userId=4`
- `GET /api/app/dashboard/advisor?userId=4`
- `GET /api/app/dashboard/manager?managerId=2`
- `GET /api/admin/dashboard/overview`
- `POST /api/app/ai/chat`
- `POST /api/app/ai/scripts/generate`
- `POST /api/app/ai/leads/extract`
- `POST /api/app/ai/leads/confirm`
- `GET /api/app/customers`
- `GET /api/app/customers/{id}`
- `POST /api/app/customers`
- `PUT /api/app/customers/{id}`
- `DELETE /api/app/customers/{id}`
- `GET /api/app/customer-tags`
- `PUT /api/app/customers/{customerId}/tags`
- `GET /api/app/customers/{customerId}/follows`
- `POST /api/app/follows`
- `GET /api/app/tasks`
- `POST /api/app/tasks`
- `PUT /api/app/tasks/{id}/complete`
- `GET /api/app/messages?userId=4`
- `PUT /api/app/messages/{id}/read`
- `PUT /api/app/messages/read-all?userId=4`
- `GET /api/app/heat/{customerId}`
- `POST /api/app/heat/calculate/{customerId}`
- `GET /api/app/heat/high-intent?advisorId=4`
- `GET /api/admin/customers`
- `GET /api/admin/tasks`
- `GET /api/admin/customer-tags`
- `POST /api/admin/customer-tags`
- `PUT /api/admin/customer-tags/{id}`
- `DELETE /api/admin/customer-tags/{id}`
- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`
- `GET /api/admin/roles`
- `GET /api/admin/depts/tree`
- `GET /api/admin/heat/high-intent?managerId=2`
- `POST /api/admin/heat/batch-calculate`
- `GET /api/admin/projects`
- `GET /api/admin/projects/{id}`
- `POST /api/admin/projects`
- `PUT /api/admin/projects/{id}`
- `DELETE /api/admin/projects/{id}`
- `GET /api/admin/projects/{projectId}/house-types`
- `POST /api/admin/projects/house-types`
- `PUT /api/admin/projects/house-types/{id}`
- `DELETE /api/admin/projects/house-types/{id}`
- `GET /api/admin/competitors`
- `GET /api/admin/competitors/{id}`
- `POST /api/admin/competitors`
- `PUT /api/admin/competitors/{id}`
- `DELETE /api/admin/competitors/{id}`

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
