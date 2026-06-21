# Airport Distance Service

A REST API that calculates the distance between two airports. You query the API with IATA codes, and it returns the distance in kilometers using the Haversine formula.

Airport data is fetched from the [AirportGap](https://airportgap.com/) API and cached using Redis.

## Tech Stack

- Java 21, Spring Boot 4
- Spring Security + JWT (access/refresh tokens)
- Redis (cache + token blacklist)
- H2 (in-memory user database)
- Resilience4j (circuit breaker, retry, rate limiter)
- Bucket4j (IP/user-based rate limiting)
- Swagger / OpenAPI
- Logback + Telegram notifications for logs

## Environment Variables

The following environment variables are required to run the project:

| Variable | Description |
|---|---|
| `JWT_SECRET_KEY` | Base64 encoded secret key for JWT signing |
| `AIRPORTGAP_URL` | AirportGap API base URL (e.g., `https://airportgap.com/api`) |
| `TELEGRAM_BOT_TOKEN` | Telegram bot token for log notifications |
| `TELEGRAM_CHAT_ID` | Telegram chat ID |

## How to Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/airport-distance-service-0.0.1-SNAPSHOT.jar
```

A running Redis instance is required (`localhost:6379`).

## API Endpoints

### Auth (`/api/v1/auth`) — public, no token required

| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Registers a new user |
| POST | `/login` | Authenticates a user and returns access & refresh tokens |
| POST | `/refresh` | Generates a new access token using a refresh token |
| POST | `/logout` | Logs out the user and blacklists the token |

### Distance (`/api/v1/distances`) — authentication required

| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Calculates distance between two airports |

**Request body:**
```json
{
  "origin": "GYD",
  "destination": "IST"
}
```

**Response:**
```json
{
  "origin": {
    "iataCode": "GYD",
    "airportName": "Heydar Aliyev International Airport",
    "city": "Baku",
    "country": "Azerbaijan"
  },
  "destination": {
    "iataCode": "IST",
    "airportName": "Istanbul Airport",
    "city": "Istanbul",
    "country": "Turkey"
  },
  "distanceInKm": 1748.07
}
```

### Admin Cache (`/api/v1/admin/cache`) — requires ADMIN role

| Method | Endpoint | Description |
|---|---|---|
| DELETE | `/airports/{iataCode}` | Clears cache for a specific airport |
| DELETE | `/distances` | Clears all cached distances |

## Rate Limiting

The service implements rate limiting on two levels:

- **Bucket4j (Filter level):** A global limit of 3 requests per minute across all endpoints (tracked by IP or user email).
- **Resilience4j (Distance endpoint):** A specific limit of 5 requests per minute for the distance calculation endpoint.

## Swagger UI

Once the application starts, you can check the API documentation at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
