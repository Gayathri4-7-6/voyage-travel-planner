# Voyage — Travel Planner

Voyage is a Spring Boot travel-planning application that helps users discover destinations, choose places to visit, and build a day-wise itinerary within a trip budget. Instead of asking users to plan every detail manually, it brings destination discovery, scheduling and estimated sightseeing expenses into one workflow. For accommodation, Voyage opens Booking.com with the travel dates and a budget-based price filter.

## What Voyage does

- **Explore destinations:** Search for a place and discover nearby attractions using location search and Wikipedia's geosearch data.
- **Choose attractions:** Select places of interest from the results, with attraction categories used for planning.
- **Generate an itinerary:** Spread selected attractions across travel dates, estimate visit duration by category, and allow for lunch and short breaks between visits.
- **Estimate sightseeing expenses:** Calculate category-based attraction costs and subtract them from the trip budget.
- **Find accommodation:** Open Booking.com with the destination, check-in date, next-day checkout date, and an estimated maximum nightly hotel budget.
- **Save and manage trips:** Store trips and their itineraries in MySQL; REST endpoints support creating, viewing, updating and deleting trips.

> Costs and visit durations are **planning estimates**, not official ticket prices, confirmed opening hours or real-time travel times. Booking.com is an external website; Voyage does not book rooms or retrieve live availability itself.

## Technology used

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring Web |
| Data access | Spring Data JPA / Hibernate |
| Database | MySQL |
| Validation | Jakarta Bean Validation |
| Frontend | HTML, CSS, JavaScript |
| External data | Nominatim (place lookup), Wikipedia MediaWiki API (nearby attractions) |
| Hotel discovery | External Booking.com search link |
| Build | Maven Wrapper (`mvnw.cmd` / `mvnw`) |

## How the application is organized

```text
backend/
├── src/main/java/com/voyage/backend/
│   ├── controller/      # HTTP endpoints
│   ├── service/         # Trip, itinerary, attraction and budget logic
│   ├── repository/      # Spring Data JPA repositories
│   ├── model/           # Database entities and embedded itinerary items
│   ├── dto/             # Objects transferred through APIs
│   └── exception/       # Custom exceptions and centralized handlers
├── src/main/resources/
│   ├── static/          # Frontend HTML, CSS and JavaScript
│   └── application.properties
└── mvnw.cmd
```

## Getting started

**Requirements:** A Java version supported by the project's `pom.xml`, MySQL Server, and a terminal (VS Code works well). Use the project's Maven Wrapper rather than installing Maven separately.

1. Clone the project and open its folder:
   ```bash
   git clone https://github.com/Gayathri4-7-6/voyage-travel-planner.git
   cd voyage-travel-planner/backend
   ```
2. In MySQL, create a database:
   ```sql
   CREATE DATABASE IF NOT EXISTS voyage_db;
   ```
3. Check `src/main/resources/application.properties` and set the datasource URL and username for your local MySQL installation. The database password should be read from an environment variable, rather than committed to Git:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/voyage_db
   spring.datasource.username=root
   spring.datasource.password=${DB_PASSWORD}
   ```
   Keep the project's existing JPA/Hibernate schema settings. For a fresh local setup, tables must be created using the configured JPA schema strategy or database migrations before saving trips.
4. Set your local database password and start the application. On **Windows PowerShell**:
   ```powershell
   $env:DB_PASSWORD="your_local_mysql_password"
   .\mvnw.cmd spring-boot:run
   ```
   On **macOS/Linux**:
   ```bash
   export DB_PASSWORD='your_local_mysql_password'
   ./mvnw spring-boot:run
   ```
5. Open **http://localhost:8080/**. The API is available under `/api/`.

## A quick walkthrough

1. Search for a destination or use a popular-destination shortcut.
2. Choose attractions and enter travel dates and a total budget.
3. Select **Plan My Trip** to create a trip, generate the itinerary and calculate the sightseeing allowance.
4. Review the day-wise visit schedule and budget breakdown.
5. Use **Find Hotels** to open Booking.com with exact search details such as destination, dates and calculated nightly limit. In Voyage's convention, the final trip date is the last sightseeing day, and hotel checkout is the following day.

The API also allows trips to be updated and deleted; see [API Documentation](docs/API.md).

## Sample API requests

Create a trip:

```bash
curl -X POST http://localhost:8080/api/trips \
  -H "Content-Type: application/json" \
  -d '{"title":"Goa Getaway","destination":"Goa","startDate":"2026-11-05","endDate":"2026-11-07","budget":15000,"description":"Explore Panaji"}'
```

Read saved trips:

```bash
curl http://localhost:8080/api/trips
```

For the complete list of endpoints, request bodies and responses, see [API Documentation](docs/API.md). For the design and database model, see [System Design](docs/DESIGN.md).

## How the project handles errors

Invalid trip fields are checked with Jakarta validation annotations. Missing trip IDs raise `TripNotFoundException`, and `GlobalExceptionHandler` translates supported failures into HTTP responses such as **400 Bad Request** and **404 Not Found**.

## Scope and limitations

Voyage is a travel-planning prototype, not a live ticketing or hotel booking service. Attraction results depend on external location/Wikipedia data and keyword-based classification. Visit times, entry fees and the hotel budget are estimates. The estimated accommodation budget is calculated by subtracting sightseeing expenses from the total trip budget. Food, transportation, and other personal expenses may not be included. Future improvements could provide more comprehensive expense estimates and warnings when the available budget is insufficient.
