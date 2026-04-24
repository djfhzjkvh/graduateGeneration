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
- `GET /api/app/ai/leads/extract-records`
- `GET /api/app/customers`
- `GET /api/app/customers/{id}`
- `GET /api/app/customers/{id}/profile`
- `POST /api/app/customers`
- `PUT /api/app/customers/{id}`
- `PUT /api/app/customers/{id}/status`
- `DELETE /api/app/customers/{id}`
- `GET /api/app/customer-tags`
- `PUT /api/app/customers/{customerId}/tags`
- `GET /api/app/customers/{customerId}/follows`
- `POST /api/app/follows`
- `GET /api/app/tasks`
- `GET /api/app/tasks/{id}`
- `POST /api/app/tasks`
- `PUT /api/app/tasks/{id}/complete`
- `PUT /api/app/tasks/{id}/delay`
- `GET /api/app/tasks/{taskId}/remind-logs`
- `GET /api/app/messages?userId=4`
- `PUT /api/app/messages/{id}/read`
- `PUT /api/app/messages/read-all?userId=4`
- `GET /api/app/heat/{customerId}`
- `POST /api/app/heat/calculate/{customerId}`
- `GET /api/app/heat/high-intent?advisorId=4`
- `POST /api/app/notes/auto-generate`
- `POST /api/app/notes`
- `GET /api/app/customers/{customerId}/notes`
- `GET /api/app/notes/{id}`
- `PUT /api/app/notes/{id}`
- `DELETE /api/app/notes/{id}`
- `POST /api/app/competitors/compare`
- `POST /api/app/competitors/ai-response`
- `GET /api/app/customers/{customerId}/competitor-history`
- `GET /api/admin/customers`
- `GET /api/admin/customers/{id}/profile`
- `GET /api/admin/customers/import/excel/template`
- `POST /api/admin/customers/import/excel/preview`
- `POST /api/admin/customers/import/excel/confirm`
- `PUT /api/admin/customers/{id}/assign`
- `GET /api/admin/customers/assign-logs`
- `GET /api/admin/customers/{id}/assign-logs`
- `PUT /api/admin/customers/{id}/status`
- `GET /api/admin/tasks`
- `GET /api/admin/tasks/{id}`
- `GET /api/admin/tasks/stat`
- `GET /api/admin/tasks/remind-logs`
- `GET /api/admin/tasks/transfer-logs`
- `GET /api/admin/tasks/{taskId}/transfer-logs`
- `PUT /api/admin/tasks/{id}/transfer`
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
- `GET /api/admin/reports/daily`
- `GET /api/admin/reports/conversion`
- `GET /api/admin/reports/source`
- `POST /api/admin/jobs/refresh-overdue-tasks`
- `POST /api/admin/jobs/generate-follow-tasks`
- `POST /api/admin/jobs/remind-due-soon-tasks`
- `POST /api/admin/jobs/generate-daily-report`
- `GET /api/admin/configs`
- `PUT /api/admin/configs/{key}`
- `GET /api/admin/ai/config`
- `GET /api/admin/ai/leads/extract-records`
- `PUT /api/admin/ai/config`
- `GET /api/admin/logs/ai`
- `GET /api/admin/logs/operation`
- `POST /api/app/files/upload`
- `POST /api/admin/files/upload`
- `GET /api/files/{id}`

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
