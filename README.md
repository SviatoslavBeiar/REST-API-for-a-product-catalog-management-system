# Product Catalog API

A RESTful API for managing an e-commerce product catalog with support for multiple producers and flexible product attributes.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Persistence | Spring Data JPA + H2 |
| Migrations | Liquibase |
| Build | Maven |
| Testing | JUnit 5 + Mockito |

---

## Architecture Decisions

### 1. Hybrid JSON Attribute Storage
Products have a `attributes` column (JSON/CLOB) alongside core structured fields (`name`, `price`, `producer_id`).

- Core fields → indexed columns for fast filtering and sorting
- Dynamic attributes → JSON bag for any number of domain-specific properties (dimensions, certifications, specs, etc.)
- **H2**: stored as `CLOB`, serialised/deserialised by `JsonAttributeConverter`
- **PostgreSQL migration**: change column type to `JSONB` for native GIN indexing

### 2. Feature-Based Packaging
```
com.catalog
├── common/         # Cross-cutting: BaseEntity, PageResponse, exceptions, converter
├── producer/       # All producer-related code in one place
└── product/        # All product-related code in one place
```

### 3. SOLID Principles Applied
| Principle | How |
|---|---|
| **S** – Single Responsibility | Controller → HTTP only; Service → business logic; Mapper → conversion; Converter → serialisation |
| **O** – Open/Closed | `ProductSpecification` adds new filter criteria without touching existing code |
| **D** – Dependency Inversion | Controllers depend on `ProducerService`/`ProductService` interfaces, not `*Impl` classes |

### 4. DRY
- `BaseEntity` — shared `createdAt`/`updatedAt` + `@PrePersist`/`@PreUpdate` hooks
- `PageResponse<T>` — generic paged wrapper used across all list endpoints
- `JsonAttributeConverter` — single JSON marshalling implementation

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+

### Run
```bash
git clone https://github.com/SviatoslavBeiar/REST-API-for-a-product-catalog-management-system.git
cd product-catalog
mvn spring-boot:run
```

The application starts at `http://localhost:8080`.
H2 Console is available at `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:catalogdb`, user: `sa`, password: empty).

### Run Tests
```bash
mvn test
```

---

## API Reference

All endpoints are prefixed with `/api/v1`.

### Products

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/products` | List all products (paginated, filterable) |
| `GET` | `/api/v1/products/{id}` | Get product by ID |
| `POST` | `/api/v1/products` | Create a new product |
| `PATCH` | `/api/v1/products/{id}` | Update product (partial) |
| `DELETE` | `/api/v1/products/{id}` | Delete product |

#### Query Parameters for `GET /api/v1/products`
| Param | Type | Description |
|---|---|---|
| `name` | string | Filter by name (case-insensitive, partial match) |
| `producerId` | long | Filter by producer |
| `minPrice` | decimal | Minimum price |
| `maxPrice` | decimal | Maximum price |
| `page` | int | Page number (default: 0) |
| `size` | int | Page size (default: 20) |
| `sortBy` | string | Sort field (default: `name`) |

### Producers

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/producers` | List all producers (paginated) |
| `GET` | `/api/v1/producers/{id}` | Get producer by ID |
| `POST` | `/api/v1/producers` | Create a new producer |
| `PATCH` | `/api/v1/producers/{id}` | Update producer (partial) |
| `DELETE` | `/api/v1/producers/{id}` | Delete producer |

---

## Example Requests

### Create a Producer
```bash
curl -X POST http://localhost:8080/api/v1/producers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "LG",
    "country": "South Korea",
    "contactEmail": "partners@lg.com"
  }'
```

### Create a Product with Rich Attributes
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "LG OLED C3 55",
    "description": "55-inch OLED 4K TV",
    "price": 1099.99,
    "producerId": 1,
    "attributes": {
      "screen_size_inch": 55,
      "resolution": "3840x2160",
      "panel_type": "OLED",
      "refresh_rate_hz": 120,
      "hdr": "Dolby Vision",
      "weight_kg": 17.2,
      "energy_rating": "A+",
      "warranty_years": 2
    }
  }'
```

### Filter Products
```bash
# Products from producer 1, price between 100–1000
curl "http://localhost:8080/api/v1/products?producerId=1&minPrice=100&maxPrice=1000&page=0&size=10"

# Search by name
curl "http://localhost:8080/api/v1/products?name=samsung"
```

### Partial Update
```bash
curl -X PATCH http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{"price": 899.99}'
```

---

## Error Responses

All errors follow [RFC 7807 Problem Detail](https://datatracker.ietf.org/doc/html/rfc7807):

```json
// 404 Not Found
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Product with id 99 not found"
}

// 400 Validation Error
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "violations": {
    "price": "Price must be positive",
    "name": "Name is required"
  }
}
```

---

## Migrating to PostgreSQL

1. Replace `pom.xml` H2 dependency with PostgreSQL driver:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/catalog
spring.datasource.username=catalog_user
spring.datasource.password=secret
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

3. Change `attributes` column in `002-create-products.xml` from `CLOB` to `jsonb`.

4. In `Product.java`, remove `@Lob` and change `@Column(columnDefinition = "jsonb")` — Hibernate will handle native JSON via the converter.

---

## Project Structure

```
src/main/java/com/catalog/
├── CatalogApplication.java
├── common/
│   ├── BaseEntity.java              # Shared audit fields (DRY)
│   ├── PageResponse.java            # Generic pagination wrapper
│   ├── converter/
│   │   └── JsonAttributeConverter.java
│   └── exception/
│       ├── ResourceNotFoundException.java
│       └── GlobalExceptionHandler.java
├── producer/
│   ├── Producer.java
│   ├── ProducerRepository.java
│   ├── ProducerService.java         # Interface (DIP)
│   ├── ProducerServiceImpl.java
│   ├── ProducerMapper.java
│   ├── ProducerController.java
│   └── dto/
│       ├── ProducerDto.java
│       ├── CreateProducerRequest.java
│       └── UpdateProducerRequest.java
└── product/
    ├── Product.java
    ├── ProductRepository.java
    ├── ProductSpecification.java    # Composable filters (OCP)
    ├── ProductService.java          # Interface (DIP)
    ├── ProductServiceImpl.java
    ├── ProductMapper.java
    ├── ProductController.java
    └── dto/
        ├── ProductDto.java
        ├── CreateProductRequest.java
        ├── UpdateProductRequest.java
        └── ProductFilter.java

src/main/resources/
├── application.properties
└── db/changelog/
    ├── master.xml
    └── changes/
        ├── 001-create-producers.xml
        ├── 002-create-products.xml
        └── 003-seed-data.xml
```
