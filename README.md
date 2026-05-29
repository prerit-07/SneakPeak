# SneakPeak MVP

Single Spring Boot web application for sneaker posts, sale/deal sharing, comments, upvotes, Google OAuth login, email verification, and sneaker-content verification.

## Run locally

Create PostgreSQL database `streetpeak`, then set environment variables as needed:

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/streetpeak"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="postgres"
$env:MAIL_HOST="localhost"
$env:MAIL_PORT="1025"
$env:GOOGLE_CLIENT_ID="your-client-id"
$env:GOOGLE_CLIENT_SECRET="your-client-secret"
$env:OPENAI_API_KEY="your-openai-api-key"
mvn spring-boot:run
```

If mail is not configured, verification links are logged to the console.
