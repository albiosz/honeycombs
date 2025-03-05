# 🐝 Honeycombs 

Honeycombs is a web-based board game assistant that automates score tracking for the game **Honeycombs**.  
This project is an opportunity for me to showcase my skills in **backend development, REST API design, security**.

# 🚀 Why this project?

I built this application because:
- I love playing **Honeycombs** but found scoring to be tedious.
- It’s a great opportunity to apply **Java 21, Spring, PostgreSQL, and Docker**.
- I want to improve my skills in **REST API development, authentication, and cloud deployment**.
- I want to explore **Spring Boot** more deeply because of its **rich ecosystem, rapid development capabilities, and built-in security**.

# 🚧 Project Status
- [x] Database schema **designed and implemented** using **Spring Data JPA**
- [x] User authentication with **JWT + Spring Security**  
- [x] API is documented with **Swagger**  
- [x] Containerized with **Docker**

🔜 Upcoming Features:
- [ ] Full **game rules & scoring logic**.
- [ ] **Frontend UI** to visualize and interact with the game.
- [ ] Deployment to a **cloud provider**.

It is possible to play with the API using Interactive Swagger Documentation (instruction below - [How to run?](README.md#how-to-run))

![Swagger API Docs](https://github.com/user-attachments/assets/52a6550d-6078-436a-b504-317756a7d5f4)


# 🚀 How to run?
1. Clone the repository.
2. `cd honeycombs-java`

You can run the project in two ways:
1. With Docker (recommended)
2. Without Docker

## With Docker (recommended)

### Prerequisites
- docker version 28.0.1 or higher

### Steps
1. `cp .env.example .env`
2. `docker compose up`
3. Go to [Play with the API](README.md#play-with-the-api) section.


## Without Docker

### Prerequisites
- PostgreSQL
- Java 21
- Maven

### Steps
1. Run Postgres
2. `cp .env.example .env`
3. Set `.env` variables according to your Postgres configuration
4. `./mvnw spring-boot:run` (Linux/Mac) or `./mvnw.cmd spring-boot:run` (Windows)
5. Go to [Play with the API](README.md#play-with-the-api) section.

## Play with the API
1. Open your browser and go to the following link to see the Swagger Documentation:
   - `http://localhost:8080/swagger-ui/index.html`
2. Login using `/api/auth/login` with default credentials.
3. Copy the token and click on the `Authorize` button in the top right corner.
4. Use other endpoints!

# 🛠️ Tech Stack

## Backend
- **Java 21**
- **Maven** (Dependency Management)
- **Spring**
  - Spring Boot
  - Spring Data JPA (Hibernate)
  - Spring Web (REST API)
  - Spring Security (JWT Authentication)

## Database
- **PostgreSQL**

## DevOps
- **Docker** (Containerized Deployment)
- **Swagger** (API Documentation)

## Testing
- **JUnit & Mockito** (Unit & Integration Testing)
- **Testcontainers** (End-to-end API Testing using real PostgreSQL instance)

# Documentation
- [Database Schema & Usecase Diagram](https://drive.google.com/file/d/1Ko0AT8uNz0bPKFwKxLztMn6k77T__1uU/view)
