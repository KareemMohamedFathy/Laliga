# LaLiga Match Intelligence API

A Spring Boot REST API for exploring LaLiga match data.

The application imports real LaLiga match data from CSV into PostgreSQL and provides endpoints for retrieving teams, matches, and basic team statistics.

## Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Apache Commons CSV
* Maven
* JUnit

## Features

* Import LaLiga match data from CSV
* Store teams and matches in PostgreSQL
* Retrieve teams with pagination
* Retrieve matches with pagination
* Retrieve a team by ID
* Retrieve a match by ID
* Retrieve all matches for a specific team
* Calculate basic aggregated team statistics
* Integration tests for the REST API

## API Endpoints

### Teams

```text
GET /teams
```

Returns teams with pagination.

Example:

```text
GET /teams?page=0&size=5
```

```text
GET /teams/{id}
```

Returns a specific team.

```text
GET /teams/{id}/matches
```

Returns matches where the selected team played either at home or away.

Example:

```text
GET /teams/1/matches?page=0&size=10
```

```text
GET /teams/{id}/stats
```

Returns aggregated statistics for a team, including:

* Matches played
* Wins
* Draws
* Losses
* Goals scored
* Goals conceded

### Matches

```text
GET /matches
```

Returns matches with pagination.

Example:

```text
GET /matches?page=0&size=10
```

```text
GET /matches/{id}
```

Returns a specific match.

## Running the Project

### 1. Create a PostgreSQL database

Create a database for the application and configure the connection in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/laliga
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 2. Add the Dataset

Place the LaLiga CSV file under:

```text
src/main/resources/data/
```

The application imports the match data into PostgreSQL.

### 3. Run the Application

Run:

```text
LaligaApplication.java
```

The API will be available at:

```text
http://localhost:8080
```

## Testing

The project contains integration tests using Spring Boot and `TestRestTemplate`.

Run the tests through Maven:

```bash
mvn test
```

or run `LaligaApplicationTests` directly from IntelliJ IDEA.

## Current Scope

This is the first version of the project and focuses on building the core backend and data model.

Authentication, advanced analytics, database performance optimization, and other features will be introduced in later versions.
