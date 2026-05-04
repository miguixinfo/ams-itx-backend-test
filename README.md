# Similar Products API

REST API that aggregates product information to expose similar products for a given product ID.

Built as a technical test based on [this specification](https://github.com/dalogax/backendDevTest).

## Overview

Given a `productId`, this service:
1. Fetches the list of similar product IDs from an existing API
2. Retrieves the detail of each similar product **in parallel**
3. Returns the aggregated list of product details

```
GET /product/{productId}/similar
```

## Tech Stack

- **Java 21**
- **Spring Boot 4.x**
- **WebClient** (reactive HTTP client for parallel calls)
- **Maven**

## Prerequisites

- Java 21+
- Maven 3.8+
- Docker (to run the mocks and load tests from the [spec repo](https://github.com/dalogax/backendDevTest))

## Running the Application

```bash
cd similar-products
./mvnw spring-boot:run
```

The app starts on port **5000**.

## Running Tests

```bash
cd similar-products
./mvnw test
```

## Running with Mocks

The mock infrastructure (`docker-compose.yml`, k6 scripts) lives in the [backendDevTest](https://github.com/dalogax/backendDevTest) spec repository. Clone it alongside this project, then:

```bash
docker-compose up -d simulado influxdb grafana
```

Verify mocks are up:

```
GET http://localhost:3001/product/1/similarids
```

Then call the API:

```
GET http://localhost:5000/product/1/similar
```

## Load Test

Run the k6 performance test (requires Docker and the spec repo):

```bash
docker-compose run --rm k6 run scripts/test.js
```

View results at [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test).

## Health Check

```
GET http://localhost:5000/actuator/health
GET http://localhost:5000/actuator/info
```

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `5000` | Port the app listens on |
| `product.api.base-url` | `http://localhost:3001` | Base URL of the existing product API |
| `product.api.timeout-seconds` | `2` | Timeout per external call |

## Architecture

Hexagonal (Ports & Adapters) pattern:

- **`domain/`** — model and exceptions, no framework dependencies
- **`port/`** — input port (`GetSimilarProductsUseCase`) and output port (`ProductPort`)
- **`application/`** — business logic (`GetSimilarProductsService`)
- **`infrastructure/`** — WebClient adapter and Spring configuration
- **`interfaces/rest/`** — REST controller, DTO, mapper, and exception handler

## Design Decisions

- **Parallel calls**: product details are fetched concurrently using `Flux.flatMap`, not sequentially
- **Resilience**: individual product failures (404, 500, timeout) are silently skipped — the response contains only the products that resolved successfully
- **Timeout**: each external call has a bounded timeout to protect against slow upstream responses

## API Contract

```
GET /product/{productId}/similar

200 OK → [ { id, name, price, availability }, ... ]
404    → product not found
```

Full contract: [similarProducts.yaml](https://github.com/dalogax/backendDevTest/blob/master/similarProducts.yaml)