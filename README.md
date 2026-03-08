# Smart Budget Tracker

Full-stack personal budget app: user registration and authentication, income/expense transactions, category statistics, and real-time currency conversion.

## Features

- **Users:** Registration, login (JWT), profile with editable username and email.
- **Transactions:** CRUD for income and expenses (amount, date, description, category, currency).
- **Dashboard:** Totals (income, expenses, balance), charts by category, monthly filters.
- **Profile:** View and edit account details (username, email).
- **Currency conversion:** Currency page – convert amounts between currencies (EUR, USD, RON, GBP, etc.) using Frankfurter exchange rates.

## Tech stack

- **Backend:** Java 17+, Spring Boot 3, Spring Data JPA, MySQL, Spring Security (JWT), Frankfurter API (exchange rates).
- **Frontend:** React 18, Vite, React Router, Axios, CSS (theme variables, dark mode ready).

## Requirements

- JDK 17 or 21
- Maven
- Node.js 18+ and npm
- MySQL (local or remote)

## Setup and run

### Backend

1. Create a MySQL database (e.g. `smart_budget`).
2. In `backend/src/main/resources/`, copy your config template (e.g. `application.properties.example`) to `application.properties` (or `application-local.properties`) and set:
   - JDBC URL, username, and password for MySQL
   - (Optional) `currency.api.base-url` if you use a different exchange-rate API base URL
3. From the `backend` directory run:
   ./mvnw spring-boot:run
   
