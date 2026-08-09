# Local Tourist Day-Visit Planner and Information System

A complete full-stack tourism information system for Rajagiriya and nearby places within a 25 km radius. The application includes public tourism browsing, place filtering, detailed map-based place pages, a simple one-day planner, and an administrator dashboard for place management.

## Tech Stack

- Frontend: HTML5, CSS3, Vanilla JavaScript
- Backend: Java 17, Spring Boot
- Database: MySQL
- Map integration: Leaflet with OpenStreetMap
- Security: Spring Security with BCrypt password hashing and admin session authentication

## Project Structure

```text
tour-planner/
├── database/
│   └── local_tourist_planner_schema.sql
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/rajagiriya/tourplanner/
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── entity/
    │   │   ├── exception/
    │   │   ├── repository/
    │   │   ├── security/
    │   │   ├── service/
    │   │   └── service/impl/
    │   └── resources/
    │       ├── application.properties
    │       ├── application-example.properties
    │       └── static/
    │           ├── css/styles.css
    │           ├── images/travel-pattern.svg
    │           ├── js/
    │           ├── admin-dashboard.html
    │           ├── admin-login.html
    │           ├── index.html
    │           ├── place-details.html
    │           ├── places.html
    │           └── planner.html
    └── test/
        └── java/com/rajagiriya/tourplanner/
```

## Database Design

### `places`

- `id`
- `name`
- `category`
- `description`
- `distance_km`
- `opening_time`
- `closing_time`
- `travel_tips`
- `address`
- `latitude`
- `longitude`
- `image_url`
- `is_active`
- `created_at`
- `updated_at`

### `admins`

- `id`
- `username`
- `password`
- `role`
- `created_at`
- `updated_at`

### `day_plans`

- `id`
- `planner_code`
- `created_at`

### `day_plan_places`

- `id`
- `day_plan_id`
- `place_id`
- `visit_order`

## Main Features

- Home page with hero section, search shortcuts, featured places, and tourism-focused presentation
- Public places listing page with category filtering and keyword search
- Place details page with image, description, travel tips, opening hours, and Leaflet map
- One-day planner with add, remove, reorder, and generate-suggested-order actions
- Secure admin login page
- Admin dashboard with summary cards and full place CRUD
- Global exception handling and validation
- Seed data for Rajagiriya-area tourism places

## Seed Data Included

The application seeds:

- 10 required places from the proposal
- 1 additional cultural place to fully cover the required category set
- Default admin account

## REST API

### Public APIs

- `GET /api/places`
- `GET /api/places/{id}`
- `GET /api/places/category/{category}`
- `GET /api/places/search?keyword=`
- `POST /api/day-plans`
- `POST /api/day-plans/{planId}/places`
- `GET /api/day-plans/{planId}`
- `DELETE /api/day-plans/{planId}/places/{placeId}`

### Extra planner convenience APIs

- `PUT /api/day-plans/{planId}/places/reorder`
- `POST /api/day-plans/{planId}/generate`

### Admin APIs

- `POST /api/admin/auth/login`
- `GET /api/admin/auth/me`
- `POST /api/admin/auth/logout`
- `GET /api/admin/places`
- `POST /api/admin/places`
- `PUT /api/admin/places/{id}`
- `DELETE /api/admin/places/{id}`

## Setup Instructions

### 1. Create the MySQL database

Option A:

- Run the SQL file in [database/local_tourist_planner_schema.sql](/Users/harshadeshappriya/Desktop/Tourist%20Planner/database/local_tourist_planner_schema.sql)

Option B:

- Let Spring Boot create the tables automatically with `spring.jpa.hibernate.ddl-auto=update`

### 2. Configure the application

Copy [src/main/resources/application-example.properties](/Users/harshadeshappriya/Desktop/Tourist%20Planner/src/main/resources/application-example.properties) values into your own environment if needed.

Default runtime properties in [src/main/resources/application.properties](/Users/harshadeshappriya/Desktop/Tourist%20Planner/src/main/resources/application.properties) expect:

- Database name: `local_tourist_planner`
- MySQL username: `root`
- MySQL password: `root`

You can override them with environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
- `ALLOWED_ORIGINS`

### 3. Start the application

```bash
mvn spring-boot:run
```

Then open:

- Public site: `http://localhost:8080/index.html`
- Admin login: `http://localhost:8080/management-portal.html`

## Sample Admin Credentials

- Username: `admin`
- Password: `Admin@123`

## Notes

- Admin passwords are stored using BCrypt hashing.
- Public pages are served from Spring Boot static resources, so no separate frontend server is required.
- The planner intentionally stays simple and demonstration-friendly.
- No hotel booking, payment, transport reservation, or real-time traffic features are included.


