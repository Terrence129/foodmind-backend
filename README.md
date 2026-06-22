# FoodMind Backend

FoodMind is an AI-powered food decision assistant designed to help users answer a simple daily question:

> What should I eat today?

The platform combines personal dining history, food preferences, friend recommendations, group food logs, and recommendation algorithms to reduce food decision fatigue and improve meal choices.

This repository contains the backend service built with Spring Boot and PostgreSQL.

---

## Features

### Authentication & User Management

* User registration and login
* JWT-based authentication
* User profile management
* Food preference configuration
* Dietary restriction management

### Food Memory System

* Meal logging
* Drink logging
* Personal food history
* Food ratings and reviews
* Repeat-consumption tracking

### Social Features

* Create food groups
* Join groups via invite code
* Group food feed
* Food sharing with privacy controls
* Friend-based food discovery

### Recommendation Engine

* Preference-based recommendations
* Repeat-avoidance scoring
* Budget-aware suggestions
* Friend-influenced recommendations
* Explainable recommendation reasons

### Analytics

* Weekly food recap
* Spending analysis
* Food frequency statistics
* Consumption pattern insights

### AI Integration (Planned)

* AI-generated recommendation explanations
* Weekly food summaries
* Group decision assistance

---

## Technology Stack

### Backend

* Java 17
* Spring Boot 3
* Spring Web
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* PostgreSQL
* Flyway

### Authentication

* JWT (JSON Web Token)
* BCrypt Password Hashing

### Documentation

* OpenAPI / Swagger

### DevOps

* Docker
* Docker Compose
* GitHub Actions (Planned)

---

## Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Project structure:

```text
src/main/java/com/foodmind

├── auth
├── user
├── profile
├── meal
├── drink
├── group
├── friendship
├── recommendation
├── analytics
├── ai
├── common
└── config
```

---

## Database Design

Main entities:

```text
users
user_profiles
cuisine_preferences

food_groups
group_members
friendships

meal_records
drink_records

recommendation_requests
recommendation_items

weekly_recaps
```

Key relationships:

```text
User
 ├── UserProfile
 ├── MealRecords
 ├── DrinkRecords
 ├── Friendships
 └── GroupMemberships

FoodGroup
 ├── GroupMembers
 ├── MealRecords
 └── DrinkRecords
```

---

## Getting Started

### Prerequisites

* Java 17+
* Maven 3.9+
* PostgreSQL 14+
* Docker Desktop (optional)

---

### Clone Repository

```bash
git clone https://github.com/yourusername/foodmind-backend.git
cd foodmind-backend
```

---

### Start PostgreSQL

Using Docker:

```bash
docker compose up -d
```

Verify:

```bash
docker ps
```

---

### Create Database

```sql
CREATE DATABASE foodmind;
```

Run migration script:

```bash
psql -U postgres -d foodmind -f V1__init_foodmind_schema.sql
```

---

### Configure Application

`application-local.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/foodmind
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=validate

jwt.secret=CHANGE_THIS_TO_A_LONG_SECRET_KEY
jwt.expiration=86400000
```

---

### Run Application

```bash
mvn spring-boot:run
```

Application:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

---

## API Modules

### Authentication

```http
POST /api/auth/register
POST /api/auth/login
```

### Profile

```http
GET  /api/profile/me
PUT  /api/profile/me
```

### Meals

```http
POST   /api/meals
GET    /api/meals
GET    /api/meals/{id}
PUT    /api/meals/{id}
DELETE /api/meals/{id}
```

### Drinks

```http
POST   /api/drinks
GET    /api/drinks
GET    /api/drinks/{id}
PUT    /api/drinks/{id}
DELETE /api/drinks/{id}
```

### Groups

```http
POST /api/groups
POST /api/groups/join
GET  /api/groups
GET  /api/groups/{id}
GET  /api/groups/{id}/feed
```

### Recommendations

```http
POST /api/recommendations/today
GET  /api/recommendations/history
POST /api/recommendations/{id}/select
POST /api/recommendations/{id}/reject
```

---

## Development Roadmap

### Phase 1

* [ ] Authentication
* [ ] User Profile
* [ ] Meal CRUD
* [ ] Drink CRUD

### Phase 2

* [ ] Group Management
* [ ] Group Feed
* [ ] Privacy Controls

### Phase 3

* [ ] Recommendation Engine
* [ ] Recommendation Feedback
* [ ] Food Analytics

### Phase 4

* [ ] AI Recommendation Explanation
* [ ] Weekly AI Recap
* [ ] AI Food Insights

### Phase 5

* [ ] AWS Deployment
* [ ] CI/CD Pipeline
* [ ] Monitoring & Logging

---

## Project Goal

FoodMind is designed as an end-to-end portfolio project demonstrating:

* Android application development
* Spring Boot backend architecture
* RESTful API design
* PostgreSQL database modeling
* Authentication and authorization
* Recommendation system design
* AI integration
* Cloud deployment and DevOps practices

---

## License

This project is developed for educational and portfolio purposes.
