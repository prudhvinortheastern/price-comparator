# Price Comparator — Project Roadmap

---

## 1. Project Description

**Price Comparator** is a browser-integrated price comparison tool that helps
online shoppers instantly discover whether a product they are viewing on Amazon
is available for less on competing retailers such as eBay and Walmart.

### Intended Purpose

The application removes the manual effort of opening multiple browser tabs to
check prices across different stores. Instead, a lightweight Chrome extension
monitors the user's Amazon browsing session and automatically queries the backend
when the user reaches their cart or checkout. If a cheaper offer exists elsewhere,
a non-intrusive banner appears on the page with a direct link to the better deal.

### Planned Audience

The primary audience is everyday online shoppers who regularly purchase products
on Amazon but want confidence that they are getting the best available price.
Secondary users are deal-hunters and budget-conscious students who want a passive
price-watchlist without installing a bloated third-party extension.

### Service Provided

- **Real-time price comparison**: On Amazon cart/checkout pages, the extension
  silently checks eBay and Walmart for the same product and surfaces the best
  competing offer.
- **User accounts & product tracking**: Users register once with a name and
  e-mail. Every product they interact with is stored with the price seen at that
  moment, building a personal price history.
- **Price-drop alerts** *(planned)*: When a tracked product's price drops below
  a user-defined threshold, a notification will be pushed to the extension.

---

## 2. Design Patterns

### Repository Pattern *(implemented)*
All database access is mediated through Spring Data JPA repository interfaces
(`UserRepository`, `ProductRepository`, `TrackedProductRepository`). Business
logic classes never write raw SQL or JPQL; they call repository methods. This
decouples persistence from logic and makes each layer independently testable.

### Strategy Pattern *(planned — final version)*
The current `CompareController` returns hardcoded mock offers. In the final
version, each retailer will be represented as a `PriceFetcher` strategy:

```
PriceFetcher (interface)
├── EbayPriceFetcher   implements PriceFetcher
└── WalmartPriceFetcher implements PriceFetcher
```

`CompareController` will iterate over all registered strategies, collect their
offers in parallel, and return the aggregated result. Adding a new retailer
(e.g., BestBuy) will require only a new class — no changes to the controller.

---

## 3. Project Outline / Status

### Completed ✅

| Component | Description |
|---|---|
| Spring Boot project scaffold | Maven build, embedded Tomcat, PostgreSQL via Neon |
| `GET /` health-check endpoint | Returns a status string; used by Render to verify uptime |
| `POST /api/auth/register` | Idempotent user registration by e-mail |
| `AuthService` validation | Rejects blank names and invalid e-mail addresses |
| `POST /api/compare` (MVP) | Returns hardcoded mock offers proving the extension↔API pipeline |
| `POST /api/tracked` | Persists user–product tracking records to PostgreSQL |
| JPA entities & repositories | `User`, `Product`, `TrackedProduct` with proper constraints |
| CORS configuration | Allows the Chrome extension to call all `/api/**` routes |
| Chrome extension — popup | User registration UI + manual compare trigger |
| Chrome extension — content script | Auto-detects Amazon cart/checkout, fires compare, injects banner |
| Render.com deployment | Live at `https://price-comparator-s4qm.onrender.com` |
| Dockerfile | Multi-stage build for containerised deployment |
| JavaDoc comments | All classes, methods, and fields documented |
| JUnit test suite (23 tests) | `AuthServiceTest`, `AuthControllerTest`, `CompareControllerTest`, `HomeControllerTest` |

---

### In Progress 🔄

| Component | Description |
|---|---|
| `content.js` production URL | Content script still points to `localhost:8080`; needs updating to the live Render URL |
| Duplicate-tracking guard | Re-submitting the same product to `POST /api/tracked` currently throws a DB constraint violation; needs an upsert fix |

---

### Planned for Final Version 📋

| Component | Priority | Description |
|---|---|---|
| **Strategy Pattern refactor** | High | Replace mock compare logic with `PriceFetcher` interface + eBay Browse API + Walmart Open API strategies |
| **Real eBay price lookup** | High | Implement `EbayPriceFetcher` using the eBay Browse API (`findItemsByKeywords`) |
| **Real Walmart price lookup** | High | Implement `WalmartPriceFetcher` using the Walmart Open API or an approved scraping approach |
| **Price history endpoint** | Medium | `GET /api/tracked/{userId}` — returns all tracked products with historical price data |
| **Price-drop alerts** | Medium | Background job compares current prices against `lastSeenPrice`; sends a badge/notification to the extension when a drop is detected |
| **Extension watchlist UI** | Medium | Popup page showing all tracked products, their last-seen prices, and current best offer |
| **Improve URL normalisation** | Low | Strip Amazon tracking parameters from URLs before storage to improve deduplication accuracy |
| **Secure credentials** | Low | Move `application.properties` DB credentials to environment variables / Render secrets dashboard |
| **Integration tests** | Low | Add `@SpringBootTest` slice tests for `TrackedProductController` using an H2 in-memory database |

---

## 4. Visual Timeline (Gantt)

```
Week  │  Task
──────┼──────────────────────────────────────────────────────────────────────
  1-2 │  ████████ Project scaffold, DB schema, auth endpoint
  3-4 │  ████████ Chrome extension MVP (popup + content script)
  5   │  ████     Deploy to Render, fix CORS, end-to-end test  [MID-SEMESTER]
──────┼──────────────────────────────────────────────────────────────────────
  6   │  ████     Fix content.js URL, duplicate-track guard
  7-8 │  ████████ Strategy Pattern refactor + eBay API integration
  9   │  ████     Walmart API integration
 10   │  ████     Price history endpoint + extension watchlist UI
 11   │  ████     Price-drop alert background job
 12   │  ████     URL normalisation, security hardening
 13   │  ████     Integration tests, bug fixes, polishing
 14   │  ████     Final demo preparation & documentation
```

---

## 5. Repository Structure

```
price-comparator/
├── src/main/java/com/example/price_comparator/
│   ├── PriceComparatorApplication.java   # Spring Boot entry point
│   ├── config/CorsConfig.java            # CORS for extension origin
│   ├── controller/
│   │   ├── HomeController.java           # Health check (GET /)
│   │   ├── AuthController.java           # User registration
│   │   ├── CompareController.java        # Price comparison (MVP stub)
│   │   └── TrackedProductController.java # Product tracking persistence
│   ├── entity/
│   │   ├── User.java
│   │   ├── Product.java
│   │   └── TrackedProduct.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ProductRepository.java
│   │   └── TrackedProductRepository.java
│   └── service/
│       └── AuthService.java              # Registration business logic
├── src/test/java/com/example/price_comparator/
│   ├── PriceComparatorApplicationTests.java
│   ├── service/AuthServiceTest.java      # 9 unit tests
│   └── controller/
│       ├── HomeControllerTest.java       # 2 tests
│       ├── AuthControllerTest.java       # 4 tests
│       └── CompareControllerTest.java    # 7 tests
├── price-comparator-extension/
│   ├── manifest.json
│   ├── popup.html / popup.js
│   └── content.js
├── Dockerfile
├── render.yaml
└── PROJECT_ROADMAP.md
```
