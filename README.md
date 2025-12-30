Fitness Microservices Application
A full-featured microservices architecture built with Spring Boot and Spring Cloud for a fitness tracking system.
This project demonstrates real-world backend skills: service discovery, distributed databases, REST APIs, and clean architecture — perfect for junior backend/Java developer portfolios.

Architecture Overview
text+------------------+
                  |   Eureka Server  |  ← Service Discovery (port 8761)
                  +--------+---------+
                           |
             +-------------+-------------+
             |                           |
+------------v------------+   +----------v----------+
|   User Service          |   |   Activity Service   |
| (PostgreSQL + JPA)      |   | (MongoDB)            |
| Port: 8081              |   | Port: 8082           |
+------------+------------+   +----------+----------+
             |                           |
             +-------------+-------------+
                           |
                  Client / Frontend

Eureka Server: Central service registry (Netflix Eureka)
User Service: Manages user registration and profiles (PostgreSQL + Spring Data JPA)
Activity Service: Tracks workouts, steps, calories, etc. (MongoDB + Spring Data MongoDB)

All services register automatically with Eureka for dynamic discovery.
Technologies Used

Java 21
Spring Boot 3.3.4
Spring Cloud Netflix Eureka (Service Discovery)
Spring Data JPA (PostgreSQL)
Spring Data MongoDB (NoSQL)
Maven (Build & Dependency Management)
Lombok (Cleaner code)
PostgreSQL & MongoDB (Local databases)

Prerequisites

Java 21 JDK
Maven
PostgreSQL (running on localhost:5432)
MongoDB (running on localhost:27017)

Setup & Running the Application
1. Start PostgreSQL and MongoDB
Make sure both databases are running locally.
Create the PostgreSQL database:
SQLCREATE DATABASE fitnessdb;
MongoDB will auto-create fitnessactivity database on first use.
2. Start the Services (in this order)
Open 3 separate terminals:
Terminal 1 – Eureka Server
Bashcd eureka-server
./mvnw spring-boot:run
→ Runs on http://localhost:8761
Terminal 2 – User Service
Bashcd userservice
./mvnw spring-boot:run
→ Runs on http://localhost:8081
Terminal 3 – Activity Service
Bashcd activityservice
./mvnw spring-boot:run
→ Runs on http://localhost:8082
3. Verify Everything Works
Open your browser and go to:
Eureka Dashboard: http://localhost:8761
You should see:
textInstances currently registered with Eureka

Application         Status
USER-SERVICE        UP (1)
ACTIVITY-SERVICE    UP (1)
Success! Your microservices are communicating and registered.
