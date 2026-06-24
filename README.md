# 🛒 Reward Point Application (Enterprise API)

![Java](https://img.shields.io/badge/Java-1.8-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7%2B-brightgreen.svg)
![Database](https://img.shields.io/badge/Database-H2-blue.svg)

A highly scalable microservice built to calculate customer reward points based on transaction history. Designed with enterprise-grade architectural patterns, this API focuses on performance, accurate financial calculations, and strict database version control.

---

## ✨ Key Enterprise Features

* **🗄️ Database Version Control:** Hibernate auto-generation (`ddl-auto`) is disabled in favor of **Flyway**, ensuring safe, version-tracked schema migrations (`V1__Init_Schema.sql`).
* **🚀 Optimized Data Access:** Completely eliminated N+1 query risks by utilizing custom JPA `@Query` with `JOIN FETCH` for efficient entity loading.
* **🧮 Precise Financial Math:** Transaction calculations leverage `BigDecimal` to prevent float-truncation issues, ensuring accurate zero-loss point calculations.
* **🧩 Design Patterns Applied:** * *DTO Pattern:* `CustomerRewardResponse` explicitly controls data exposure.
    * *Utility Pattern:* Centralized, static calculation logic for easy testing and maintenance.
* **🌱 Deterministic Data Seeding:** Automatically detects empty environments and bootstraps reproducible mock data (5 customers, realistic decimal transactions) using a fixed random seed for consistent API testing.
* **🚦 Graceful Exception Handling:** A `@RestControllerAdvice` global handler catches everything from validation errors to generic server errors, returning clean, standardized JSON with accurate HTTP status codes (e.g., `404 Not Found` for empty records, `400 Bad Request` for invalid dates).
* **🧪 Comprehensive Testing:** Extensive suite utilizing JUnit, Mockito, and JaCoCo covering business logic accuracy, edge-case decimal handling, isolated service testing, and full-stack `@SpringBootTest` integration tests.

---

## 🛠️ Technology Stack

* **Core:** Java 1.8, Spring Boot (Web, Data JPA)
* **Database:** H2 (Local/Dev), Flyway (Migrations)
* **Tools:** Swagger/OpenAPI 3.0, Maven, JaCoCo (Coverage)

---

## 🚀 Getting Started & How to Run

### Prerequisites
* JDK 1.8 installed
* Maven 3.9+ installed (or use the included `mvnw` wrapper)

### Local Development Run (H2 Database)

**Clone the repository:**

git clone [https://github.com/YOUR_GITHUB_USERNAME/reward-point-application.git](https://github.com/YOUR_GITHUB_USERNAME/reward-point-application.git)

cd reward-point-application

Compile and run the application via Terminal : mvnw clean spring-boot:run

**Via IDE:** Open the project in IntelliJ or Spring Tool Suite 4 and run the main application class.

**Access the API:** The server will start on http://localhost:8080.
(Note: Flyway will automatically execute and build the H2 database schema on boot).

📚 **API Documentation & Endpoints:**
Once the application is running, you can access the interactive OpenAPI documentation and the database:

**Swagger UI:** http://localhost:8080/swagger-ui/index.html

**H2 Database Console:** http://localhost:8080/h2-console

**JDBC URL:** jdbc:h2:mem:rewardsdb

**User:** sa (No password)

🏗️ **Calculate Rewards for All Customers**

Calculates the reward points for all customers. If no dates are provided, it defaults to evaluating the last 3 months of transactions.

**Endpoint:** GET /api/v1/rewards

**With Custom Dates:**
GET /api/v1/rewards?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD

   ```bash
Sample Request 1 : (Without Date)

curl --location 'http://localhost:8080/api/v1/rewards' \
--header 'accept: */*'

Sample Response 1 :

[
  {
    "customerId": "C1",
    "customerName": "Customer 1",
    "evaluationPeriod": "2026-03-22 to 2026-06-22",
    "totalTransactionsProcessed": 5,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 50
      },
      {
        "month": "May",
        "year": "2026",
        "points": 0
      },
      {
        "month": "June",
        "year": "2026",
        "points": 143.98
      }
    ],
    "totalRewardPoints": 193.98
  },
  {
    "customerId": "C3",
    "customerName": "Customer 3",
    "evaluationPeriod": "2026-03-22 to 2026-06-22",
    "totalTransactionsProcessed": 4,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 8.88
      },
      {
        "month": "May",
        "year": "2026",
        "points": 103.26
      },
      {
        "month": "June",
        "year": "2026",
        "points": 31.85
      }
    ],
    "totalRewardPoints": 143.99
  },
  {
    "customerId": "C4",
    "customerName": "Customer 4",
    "evaluationPeriod": "2026-03-22 to 2026-06-22",
    "totalTransactionsProcessed": 4,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 134.62
      },
      {
        "month": "May",
        "year": "2026",
        "points": 179.66
      },
      {
        "month": "June",
        "year": "2026",
        "points": 40.94
      }
    ],
    "totalRewardPoints": 355.22
  },
  {
    "customerId": "C5",
    "customerName": "Customer 5",
    "evaluationPeriod": "2026-03-22 to 2026-06-22",
    "totalTransactionsProcessed": 5,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 22.66
      },
      {
        "month": "May",
        "year": "2026",
        "points": 20.62
      },
      {
        "month": "June",
        "year": "2026",
        "points": 140.78
      }
    ],
    "totalRewardPoints": 184.06
  }
]
   
Sample Request 2 : (With Custom Date)

curl --location 'http://localhost:8080/api/v1/rewards?startDate=2026-03-17&endDate=2026-06-15' \
--header 'accept: */*'

Sample Response 2 :

[
  {
    "customerId": "C1",
    "customerName": "Customer 1",
    "evaluationPeriod": "2026-03-17 to 2026-06-15",
    "totalTransactionsProcessed": 3,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 50
      },
      {
        "month": "May",
        "year": "2026",
        "points": 0
      },
      {
        "month": "June",
        "year": "2026",
        "points": 91.98
      }
    ],
    "totalRewardPoints": 141.98
  },
  {
    "customerId": "C3",
    "customerName": "Customer 3",
    "evaluationPeriod": "2026-03-17 to 2026-06-15",
    "totalTransactionsProcessed": 4,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 8.88
      },
      {
        "month": "May",
        "year": "2026",
        "points": 103.26
      },
      {
        "month": "June",
        "year": "2026",
        "points": 31.85
      }
    ],
    "totalRewardPoints": 143.99
  },
  {
    "customerId": "C4",
    "customerName": "Customer 4",
    "evaluationPeriod": "2026-03-17 to 2026-06-15",
    "totalTransactionsProcessed": 4,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 134.62
      },
      {
        "month": "May",
        "year": "2026",
        "points": 179.66
      },
      {
        "month": "June",
        "year": "2026",
        "points": 40.94
      }
    ],
    "totalRewardPoints": 355.22
  },
  {
    "customerId": "C5",
    "customerName": "Customer 5",
    "evaluationPeriod": "2026-03-17 to 2026-06-15",
    "totalTransactionsProcessed": 5,
    "pointsByMonth": [
      {
        "month": "April",
        "year": "2026",
        "points": 22.66
      },
      {
        "month": "May",
        "year": "2026",
        "points": 20.62
      },
      {
        "month": "June",
        "year": "2026",
        "points": 140.78
      }
    ],
    "totalRewardPoints": 184.06
  }
]

🏗️ **Future Enhancements:**
Resilience: Implement Resilience4j circuit breakers to prevent cascading failures during heavy database load.

Developed by Jitendra Sharma.