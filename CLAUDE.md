# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Price Comparator is a full-stack app: a **Spring Boot backend** (Java 17, Maven) + a **Chrome Extension** (Manifest V3). The extension auto-detects products on Amazon and calls the backend to find cheaper alternatives on eBay/Walmart.

Live backend: `https://price-comparator-s4qm.onrender.com` (free tier — 15-min inactivity spin-down)

## Commands

### Backend

```bash
# Run locally
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=AuthServiceTest

# Build JAR (skip tests)
./mvnw clean package -DskipTests

# Docker build + run
docker build -t price-comparator . && docker run -p 8080:8080 price-comparator
```

### Chrome Extension

Load unpacked from `price-comparator-extension/` in Chrome's `chrome://extensions` (Developer mode on). No build step required.

## Architecture

### Backend (`src/main/java/com/example/price_comparator/`)

Layered Spring Boot app:
- **Controllers** → **Services** → **Repositories** (Spring Data JPA) → **PostgreSQL (Neon)**
- Tests use H2 in-memory DB; production uses Neon via `application.properties`

Key API endpoints:
| Method | Path | Purpose |
|--------|------|---------|
| GET | `/` | Health check |
| POST | `/api/auth/register` | Register/retrieve user by email (idempotent) |
| POST | `/api/compare` | Compare product price (currently returns mock eBay/Walmart data) |
| POST | `/api/tracked` | Track a product for a user |

`/api/compare` is designed for a **Strategy Pattern** refactor — mock `PriceFetcher` implementations will be replaced with real eBay Browse API and Walmart Open API integrations.

### JPA Entities
- **User**: id, name, email (unique, case-insensitive), createdAt
- **Product**: id, url (unique), title, site, createdAt
- **TrackedProduct**: id, user_id (FK), product_id (FK), lastSeenPrice, createdAt — unique on (user_id, product_id)

### Chrome Extension (`price-comparator-extension/`)
- **`content.js`**: Runs on `*.amazon.com` pages, auto-detects cart/checkout, calls `/api/compare`, injects a banner if cheaper offers found
- **`popup.js`/`popup.html`**: User registration UI, stores user in Chrome local storage, manual compare trigger

CORS is configured to allow all origins on `/api/**` (intentionally permissive for the extension).

## Known Issues

- **Duplicate tracking**: Re-submitting the same product via `POST /api/tracked` throws a DB constraint violation — needs upsert logic.
- **Mock price data**: `/api/compare` returns hardcoded offers; real API integrations are planned.
- **Credentials in code**: `application.properties` contains plaintext Neon DB credentials — these should move to environment variables before production hardening.

## Testing

Controller tests use `@WebMvcTest` + `MockMvc`. Service tests use `@ExtendWith(MockitoExtension.class)`. All 23 tests run against H2 (no external DB needed for tests).
