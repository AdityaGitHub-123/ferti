# FertiSmart - Fertilizer Recommendation Rule Engine

A modern full-stack agriculture platform built with Java, Spring Boot, JDBC, MySQL, Apache Tomcat, HTML, CSS, and JavaScript.

## Features

- Login, signup, logout, and forgot password UI
- BCrypt encrypted password storage
- REST APIs for authentication, soil submission, crops, dashboard stats, and recommendation history
- JDBC repositories using `PreparedStatement`
- MySQL database `fertilizer_db`
- Rule engine:
  - Low nitrogen recommends Urea
  - Low phosphorus recommends DAP
  - Low potassium recommends MOP
  - Balanced soil recommends Organic Compost
- Soil health status: Healthy, Medium, Poor
- Responsive dashboard, sidebar, profile dropdown, history table, toasts, loading animation, dark/light mode
- Dynamic fertilizer and crop images in recommendation cards

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web REST APIs
- Spring JDBC
- MySQL
- Apache Tomcat embedded with Spring Boot
- HTML, CSS, JavaScript

## Database

The app uses `fertilizer_db` and auto-runs `src/main/resources/schema.sql` on startup.

Default database settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fertilizer_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

Update the username and password to match your local MySQL installation.

## Run

Install Java 17, Maven, and MySQL first. Then update `application.properties` with your MySQL password.

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## Windows Local MySQL

This project is configured for:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fertilizer_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

If MySQL is not already running after a reboot, start it from the project folder:

```powershell
.\scripts\start-mysql.ps1
```

To start MySQL and run Spring Boot together:

```powershell
.\scripts\run-app.ps1
```

## Main API Endpoints

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `POST /api/auth/forgot-password`
- `POST /api/soil`
- `GET /api/recommendations`
- `GET /api/recommendations/recent`
- `GET /api/dashboard/stats`
- `GET /api/crops`

Authenticated API calls use the `X-Auth-Token` header returned from login/signup.

## Project Structure

```text
src/main/java/com/fertilizer/ruleengine
  controller
  dto
  entity
  exception
  repository
  service

src/main/resources/static
  css
  js
  images
  *.html
```
