<div align="center">

<img src="./liqaa-logo.png" alt="Liqaa Logo" width="180">

# Liqaa | لقاء

### Smart Meeting Point Recommendation Platform

A backend platform that helps groups organize meetings, find fair meeting locations, receive AI-ranked place recommendations, vote, and confirm the final destination.

<br>

<img src="./tuwaiq-academy-logo.png" alt="Tuwaiq Academy" width="160">

**Java & Spring Boot Capstone Project — Tuwaiq Academy**

</div>

---

## About Liqaa

**Liqaa** is a meeting coordination platform designed to simplify one common problem: **Where should a group meet?**

The platform allows users to create meetings, invite participants, collect their locations, calculate a central meeting area, discover real nearby places through Google Places, calculate distance-based fairness, rank recommendations using AI, vote, and confirm the final destination.

---

## Meeting Flow

```text
Create Meeting Request
        ↓
Invite Members / Generate Invite Code
        ↓
Members Join
        ↓
Start Meeting
        ↓
Participants Submit Locations
        ↓
Calculate Geographic Center
        ↓
Fetch Nearby Places
        ↓
Calculate Distance & Fairness
        ↓
AI Ranking
        ↓
Select Places for Voting
        ↓
Start Voting
        ↓
Participants Vote
        ↓
Confirm Final Place
        ↓
Send Google Maps Link by Email
```

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot |
| REST API | Spring Web |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Validation |
| AI Integration | Spring AI |
| AI Provider | OpenRouter |
| Places | Google Places API |
| Distance Calculation | Haversine Formula |
| Email | Spring Mail |
| Utilities | Lombok |
| Build Tool | Maven |

---

## Main Features

- User management and validation
- Meeting request lifecycle
- Organizer-based authorization
- Email invitations
- Public invite codes
- Participant management
- Participant location collection
- Geographic meeting-center calculation
- Real place discovery using Google Places
- Participant-to-place distance calculation
- Fairness scoring
- AI-assisted recommendation ranking
- Place nomination
- Participant voting
- Vote changing
- Automatic winner selection
- Fairness-based tie breaking
- Final meeting confirmation
- Google Maps link sent to participants by email

---

## Architecture

The project follows a layered Spring Boot architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

External integrations are separated into dedicated clients and services:

```text
MeetingService
├── GooglePlacesClient
├── OpenRouterClient
├── DistanceService
├── FairnessService
└── EmailService
```

### Project Structure

```text
src/main/java/com/nawaf/meetingpoint
│
├── Client
├── Controller
├── DTO
├── Model
├── Repository
├── Service
└── MeetingPointApplication.java
```

| Layer | Responsibility |
|---|---|
| Controller | REST endpoints and HTTP responses |
| Service | Business logic and authorization |
| Repository | Database access |
| Model | JPA entities |
| DTO | Request, response, and external API objects |
| Client | External API communication |

---

# Database

## Tables

| Table | Description |
|---|---|
| `user` | Stores registered users |
| `meeting_request` | Stores meeting requests and organizer information |
| `meeting_request_member` | Stores invited members and invitation status |
| `meeting` | Stores active meetings and selected places |
| `participant` | Stores users participating in meetings |
| `place` | Stores places selected for voting |
| `participant_place_route` | Stores calculated distances between participants and places |
| `vote` | Stores participant votes |

---

## Relationships

```text
User
 ├── MeetingRequest
 └── Participant

MeetingRequest
 ├── MeetingRequestMember
 └── Meeting

Meeting
 ├── Participant
 ├── Place
 └── Selected Place

Participant
 ├── ParticipantPlaceRoute
 └── Vote

Place
 ├── ParticipantPlaceRoute
 └── Vote
```

---

## Status Values

| Entity | Available Status |
|---|---|
| Meeting Request | `PENDING`, `READY`, `CANCELLED` |
| Meeting Request Member | `PENDING`, `ACCEPTED`, `REJECTED` |
| Meeting | `OPEN`, `VOTING`, `CONFIRMED`, `CANCELLED` |

---

# Recommendation System

Liqaa combines deterministic calculations with AI-assisted ranking.

The AI does **not** generate or invent meeting places.

### Recommendation Process

1. Collect participant coordinates.
2. Calculate the geographic center.
3. Retrieve real nearby places from Google Places.
4. Calculate the distance from every participant to every place.
5. Calculate average distance, maximum distance, and fairness score.
6. Send the real places and calculated metrics to OpenRouter.
7. Rank the recommendations using AI.

### Distance Calculation

The system uses the **Haversine Formula** to calculate straight-line geographic distance between participant coordinates and candidate places.

### Fairness

Each candidate place receives a fairness score based on how evenly distributed participant distances are.

The score is calculated by the backend before AI ranking.

---

# API Reference

Base URL:

```text
http://localhost:8080/api/v1
```

---

## User

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/user/get-all` | Get all users |
| `POST` | `/user/add` | Create a new user |
| `GET` | `/user/get/{id}` | Get user by ID |
| `PUT` | `/user/update/{id}` | Update user |
| `DELETE` | `/user/delete/{id}` | Delete user |

---

## Meeting Request

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/meeting-request/get-all` | Get all meeting requests |
| `POST` | `/meeting-request/add` | Create meeting request |
| `GET` | `/meeting-request/get/{id}` | Get meeting request by ID |
| `PUT` | `/meeting-request/update/{id}/{organizerId}` | Update meeting request |
| `PUT` | `/meeting-request/cancel/{id}/{organizerId}` | Cancel meeting request |
| `DELETE` | `/meeting-request/delete/{id}/{organizerId}` | Delete meeting request |

---

## Invite Code

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/invite-code/generate/{requestId}/{organizerId}` | Generate invite code |
| `PUT` | `/invite-code/regenerate/{requestId}/{organizerId}` | Generate a new invite code |
| `DELETE` | `/invite-code/disable/{requestId}/{organizerId}` | Disable invite code |
| `GET` | `/invite-code/get/{inviteCode}` | Find meeting request using invite code |

---

## Meeting Request Member

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/meeting-request-member/invite/{requestId}/{organizerId}` | Invite member by email |
| `POST` | `/meeting-request-member/join/{inviteCode}/{userId}` | Join using invite code |
| `PUT` | `/meeting-request-member/accept/{memberId}/{userId}` | Accept invitation |
| `PUT` | `/meeting-request-member/reject/{memberId}/{userId}` | Reject invitation |
| `GET` | `/meeting-request-member/accepted/{requestId}` | Get accepted members |

---

## Meeting

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/meeting/get-all` | Get all meetings |
| `GET` | `/meeting/get/{id}` | Get meeting by ID |
| `POST` | `/meeting/start/{requestId}/{organizerId}` | Start meeting |
| `PUT` | `/meeting/cancel/{id}/{organizerId}` | Cancel meeting |
| `GET` | `/meeting/{meetingId}/missing-locations` | Get participants without locations |
| `GET` | `/meeting/{meetingId}/location-readiness` | Check whether all locations are available |
| `POST` | `/meeting/{meetingId}/calculate-center/{organizerId}` | Calculate geographic center |
| `GET` | `/meeting/{meetingId}/calculate-recommendations` | Calculate place recommendations |
| `POST` | `/meeting/{meetingId}/generate-recommendations` | Generate AI-ranked recommendations |
| `PUT` | `/meeting/start-voting/{id}/{organizerId}` | Start voting |
| `PUT` | `/meeting/confirm/{meetingId}/{organizerId}` | Confirm final meeting place |

---

## Participant

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/participant/meeting/{meetingId}` | Get meeting participants |
| `GET` | `/participant/get/{id}` | Get participant by ID |
| `POST` | `/participant/add/{meetingId}/{organizerId}/{userId}` | Add participant |
| `PUT` | `/participant/location/{participantId}/{userId}` | Update participant location |
| `DELETE` | `/participant/delete/{meetingId}/{organizerId}/{participantId}` | Remove participant |

---

## Place

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/place/meeting/{meetingId}` | Get places selected for voting |
| `GET` | `/place/get/{id}` | Get place by ID |
| `POST` | `/place/meeting/{meetingId}/select/{participantId}/{userId}` | Add recommendation to voting |
| `DELETE` | `/place/meeting/{meetingId}/remove/{organizerId}/{placeId}` | Remove place from voting |

---

## Vote

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/vote/{meetingId}/{participantId}/{userId}/{placeId}` | Submit vote |
| `PUT` | `/vote/{meetingId}/{participantId}/{userId}/{placeId}` | Change vote |
| `GET` | `/vote/place/{placeId}` | Get votes for place |
| `GET` | `/vote/meeting/{meetingId}/result` | Get meeting voting result |

---

## Participant Place Route

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/route/get/{id}` | Get route record |
| `GET` | `/route/place/{placeId}` | Get participant distances for place |
| `GET` | `/route/participant/{participantId}` | Get distances for participant |

---

# Authorization

Authorization rules are enforced inside the service layer.

| Action | Authorized User |
|---|---|
| Update meeting request | Organizer |
| Delete meeting request | Organizer |
| Cancel meeting request | Organizer |
| Manage invite code | Organizer |
| Invite members | Organizer |
| Start meeting | Organizer |
| Cancel meeting | Organizer |
| Calculate meeting center | Organizer |
| Add/remove participants | Organizer |
| Start voting | Organizer |
| Confirm meeting | Organizer |
| Update participant location | Participant owner |
| Nominate voting place | Participant |
| Submit/change vote | Participant owner |

---

# External Services

## Google Places API

Google Places is used to retrieve real locations around the calculated meeting center.

Supported categories include:

```text
cafe
restaurant
park
movie_theater
shopping_mall
```

---

## OpenRouter

OpenRouter is integrated through Spring AI.

It receives the places returned by Google Places together with:

- Rating
- Average participant distance
- Maximum participant distance
- Fairness score

It then returns:

- AI rank
- AI score
- Ranking reason

---

## Email Service

Spring Mail is used to send:

| Email | Trigger |
|---|---|
| Welcome Email | User account created |
| Meeting Invitation | Organizer invites a member |
| Meeting Confirmation | Final meeting place confirmed |
| Google Maps Link | Included in confirmation email |

The final Google Maps URL uses the Google Place ID so participants can open the exact selected place.

---

# Configuration

Required environment variables:

```env
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

GOOGLE_MAPS_API_KEY=your_google_maps_api_key

OPENROUTER_API_KEY=your_openrouter_api_key

MAIL_HOST=your_mail_host
MAIL_PORT=your_mail_port
MAIL_USERNAME=your_mail_username
MAIL_PASSWORD=your_mail_password
```

Do not commit API keys, passwords, or other credentials to the repository.

---

# Getting Started

## Clone the Repository

```bash
git clone https://github.com/Inawaf9/capstone2.git
cd capstone2
```

## Create Database

```sql
CREATE DATABASE meeting_point;
```

## Run the Application

### macOS / Linux

```bash
./mvnw spring-boot:run
```

### Windows

```bash
mvnw.cmd spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

---

# Author

**Nawaf Alghamdi**

Software Engineering  
Java & Spring Boot  
Tuwaiq Academy

---

<div align="center">

### Liqaa | لقاء

**Finding a fair meeting point for everyone.**

</div>
