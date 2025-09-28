# 🧠 Chatbot Backend – Spring Boot + PostgreSQL + Groq API

This is a backend service for  **Chatbot Service**, built with:

* **Spring Boot**
* **PostgreSQL / H2 (for dev & tests)**
* **Groq API integration**
* **Bucket4j rate limiting**
* **API Key security filter**
* **Swagger + Actuator health endpoint**
* **Docker + docker-compose deployment**

---


## ⚙️ Environment Variables

We use a `.env` file to hold sensitive data.
An example is provided in **`.env.example`**:

```env
# Database
DATASOURCE_USER=admin
DATASOURCE_PASSWORD=admin
DATASOURCE_URL=jdbc:postgresql://db:5432/chatdb

# API Keys
GROQ_API_KEY=your_groq_api_key

# External API Keys
GROQ_API_KEY=your_groq_api_key_here

# Rate limiting
APP_RATE_CAPACITY=10
APP_RATE_REFILL_TOKENS=10
APP_RATE_REFILL_PERIOD_SECONDS=60
```

Copy it before running:

```bash
cp .env.example .env
```

Update the `.env` file with the real values.


---


## 🚀 Run the Service Locally

### Step 1: Build the app

```bash
mvn clean package
```

### Step 2: Run it locally with H2 (default)

```bash
java -jar target/chatbot-0.0.1-SNAPSHOT.jar
```

By default, it will use:

* `H2 in-memory database`
* properties from `application-dev.yml`
* fallback values from `.env` (if defined)

---

## 🐯 Run with Docker Compose

We now support full orchestration with **docker-compose**.

### Step 1: Build the app

```bash
mvn clean package -DskipTests
```

### Step 2: Start everything

```bash
docker-compose up --build
```

This will start:

* `chatbot-db` → PostgreSQL 15
* `chatbot-app` → Spring Boot service (port 8080 → 8080)

### Step 3: Verify

* Health Check: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

###
🧼 Cleanup Docker Resources

```bash
docker-compose down -v
```

---

## 🏗️ Run with Production Profile

For production deployments, use the prod profile, which loads settings from application-prod.yaml.

### Option 1: Run with JVM arguments

```bash
java -jar target/chatbot-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --DB_URL=jdbc:postgresql://rds-host:5432/chatdb \
  --DATASOURCE_USER=admin \
  --DATASOURCE_PASSWORD=secure123 \
  --HIBERNATE_DDL=update \
  --GROQ_API_KEY=your_real_groq_key \
  --API_KEY=secure_api_key

```

### Option 2: Use Environment Variables

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://rds-host:5432/chatdb
export DATASOURCE_USER=admin
export DATASOURCE_PASSWORD=secure123
export HIBERNATE_DDL=update
export GROQ_API_KEY=your_real_groq_key
export API_KEY=secure_api_key

java -jar target/chatbot-0.0.1-SNAPSHOT.jar
```

### Option 3: Use .env.prod file

Create a .env.prod file with:

```bash
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://rds-host:5432/chatdb
DATASOURCE_USER=admin
DATASOURCE_PASSWORD=secure123
HIBERNATE_DDL=update
GROQ_API_KEY=your_real_groq_key
API_KEY=secure_api_key
```

Run the service with Docker:

```bash
docker run --env-file .env.prod chatbot-app
```

---
## ✅ Features

* **Chat Sessions API** → create, rename, delete, mark favorite
* **Chat Messages API** → send messages, integrates with Groq API, persists replies
* **Rate Limiting** → prevents abuse (per IP, Bucket4j configurable via `.env`)
* **Security Filter** → requires `X-API-KEY` header
* **Swagger + Actuator** → API documentation & health check

---

## 📖 Example Usage (via `curl`)

### 1️⃣ Create a new session

```bash
curl -X POST http://localhost:8080/chat/session \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your_api_key_here" \
  -d '{"name": "My Test Session"}'
```

### 2️⃣ Send a message

```bash
curl -X 'POST' \
  'http://localhost:8080/chat/session/messages' \
  -H 'accept: */*' \
  -H 'X-API-KEY: your_api_key_here' \
  -H 'Content-Type: application/json' \
  -d '{
  "sessionId": "15411a6e-17a2-4900-a276-24edad6c301b",
  "userMessage": "Hello AI"
}'
```

### 3️⃣ Get messages for a session

```bash
curl -X GET "http://localhost:8086/chat/session/messages/your-session-id-here?page=1&size=10" \
  -H "X-API-KEY: your_api_key_here"
```

### 4️⃣ Rename a Session

```bash
curl -X 'PUT' \
  'http://localhost:8080/chat/session/{sessionId}/rename' \
  -H 'accept: */*' \
  -H 'X-API-KEY: your_api_key_here' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Renamed Session"
}'
```

5️⃣ Mark Session as Favorite

```bash
curl -X 'PUT' \
  'http://localhost:8080/chat/session/{sessionId}/favorite?favorite=true' \
  -H 'accept: */*' \
  -H 'X-API-KEY: your_api_key_here'
```


6️⃣ Delete a Session

```bash
curl -X 'DELETE' \
  'http://localhost:8080/chat/session/{sessionId}' \
  -H 'accept: */*' \
  -H 'X-API-KEY: your_api_key_here'
```

7️⃣ Health Check

```bash
curl -X GET http://localhost:8080/actuator/health \
  -H "X-API-KEY: your_api_key_here"
```
---

## 🧪 Testing

We provide **unit tests** for:

* **API layer (controllers)** using MockMvc with mocked services
* **Service layer** with mocked repositories

📌 Note: **No integration tests** with a real DB yet. H2 is only used for local runs and could be extended later.

Run tests:

```bash
mvn test
```

---

## 🧼 Cleanup Docker Resources

```bash
docker-compose down -v
```

---

## 🗁️ Profiles Summary

| Profile  | Description                 | DB          |
| -------- | --------------------------- | ----------- |
| `dev`    | Local dev with H2           | H2 (Memory) |
| `docker` | Docker container + Postgres | PostgreSQL  |
| `prod`   | RDS/PostgreSQL              | PostgreSQL  |

---
