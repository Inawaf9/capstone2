::: {align="center"}
`<img src="liqaa-logo.png" alt="Liqaa Logo" width="220"/>`{=html}

# Liqaa \| لقاء

### Smart Meeting Point Recommendation Platform

A Spring Boot backend that helps groups organize meetings, collect
participant locations, discover nearby places, rank recommendations
fairly with AI, vote, and confirm the final meeting place.

`<br/>`{=html}

`<img src="tuwaiq-academy-logo.png" alt="Tuwaiq Academy" width="180"/>`{=html}

**Java & Spring Boot Capstone Project --- Tuwaiq Academy**
:::

------------------------------------------------------------------------

## Overview

**Liqaa** is a meeting coordination platform designed to simplify the
process of choosing a fair meeting place for a group.

The system manages invitations and participants, calculates a geographic
center from participant locations, retrieves real nearby places using
Google Places, calculates distance-based fairness, uses AI to rank
suitable recommendations, supports participant voting, and confirms the
final destination.

### Core Flow

``` text
Create Meeting Request
        ↓
Invite Members / Share Invite Code
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
Participants Nominate Places
        ↓
Voting
        ↓
Confirm Final Place
        ↓
Email Participants with Google Maps Link
```

------------------------------------------------------------------------

## Tech Stack

  Area          Technology
  ------------- -----------------------------
  Language      Java 21
  Framework     Spring Boot
  API           Spring MVC / REST
  Persistence   Spring Data JPA / Hibernate
  Database      MySQL
  Validation    Jakarta Validation
  AI            Spring AI + OpenRouter
  Places        Google Places API
  Email         Spring Mail
  Build Tool    Maven
  Utilities     Lombok

------------------------------------------------------------------------

## Key Features

-   User management with validation and welcome emails
-   Meeting request lifecycle and organizer authorization
-   Direct invitations and reusable invite codes
-   Participant location collection
-   Geographic center calculation
-   Real place discovery through Google Places
-   Haversine distance calculation between participants and places
-   Fairness scoring based on participant distances
-   AI-assisted place ranking through OpenRouter
-   Participant place nomination
-   Voting and vote changes
-   Automatic winner selection
-   Fairness-based tie breaking
-   Final meeting confirmation
-   Google Maps place link sent to all participants by email

------------------------------------------------------------------------

## Architecture

The project follows a layered Spring Boot architecture:

``` text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

External integrations are isolated from business logic:

``` text
MeetingService
├── GooglePlacesClient
├── OpenRouterClient
├── DistanceService
├── FairnessService
└── EmailService
```

This keeps HTTP handling, business rules, persistence, calculations, and
external APIs separated.

------------------------------------------------------------------------

## Database Model

### Main Tables

  -----------------------------------------------------------------------
  Table                               Purpose
  ----------------------------------- -----------------------------------
  `user`                              Registered users

  `meeting_request`                   Meeting creation request and
                                      organizer information

  `meeting_request_member`            Invited members and invitation
                                      status

  `meeting`                           Active meeting and final selected
                                      place

  `participant`                       Users participating in a started
                                      meeting

  `place`                             Places nominated for voting

  `participant_place_route`           Calculated participant-to-place
                                      distances

  `vote`                              Participant votes
  -----------------------------------------------------------------------

### Relationships

``` text
User
  │
  ├── organizes ──> MeetingRequest
  │
  └── participates ──> Participant

MeetingRequest
  │
  ├── has ──> MeetingRequestMember
  │
  └── creates ──> Meeting

Meeting
  │
  ├── has ──> Participant
  ├── has ──> Place
  └── selects ──> Place

Participant
  │
  ├── has ──> ParticipantPlaceRoute
  └── casts ──> Vote

Place
  │
  ├── has ──> ParticipantPlaceRoute
  └── receives ──> Vote
```

### Statuses

  Entity                   Statuses
  ------------------------ --------------------------------------------
  Meeting Request          `PENDING`, `READY`, `CANCELLED`
  Meeting Request Member   `PENDING`, `ACCEPTED`, `REJECTED`
  Meeting                  `OPEN`, `VOTING`, `CONFIRMED`, `CANCELLED`

------------------------------------------------------------------------

## Recommendation Logic

Liqaa does not ask AI to invent meeting places.

The recommendation pipeline is:

1.  Collect all participant coordinates.
2.  Calculate the average geographic center.
3.  Request real nearby places from Google Places.
4.  Calculate straight-line distance from every participant to every
    candidate place using the Haversine formula.
5.  Calculate average distance, maximum distance, and fairness score.
6.  Send only the real candidate places and calculated metrics to
    OpenRouter.
7.  Return the AI ranking together with the deterministic distance and
    fairness data.

This keeps location discovery and mathematical calculations independent
from the AI ranking layer.

------------------------------------------------------------------------

## API Endpoints

Base URL:

``` text
http://localhost:8080/api/v1
```

### Users

  Method   Endpoint              Description
  -------- --------------------- ----------------
  GET      `/user/get-all`       Get all users
  POST     `/user/add`           Create a user
  PUT      `/user/update/{id}`   Update a user
  DELETE   `/user/delete/{id}`   Delete a user
  GET      `/user/get/{id}`      Get user by ID

### Meeting Requests

  ----------------------------------------------------------------------------------------------
  Method                  Endpoint                                       Description
  ----------------------- ---------------------------------------------- -----------------------
  GET                     `/meeting-request/get-all`                     Get meeting requests

  POST                    `/meeting-request/add`                         Create meeting request

  PUT                     `/meeting-request/update/{id}/{organizerId}`   Update meeting request

  DELETE                  `/meeting-request/delete/{id}/{organizerId}`   Delete meeting request

  GET                     `/meeting-request/get/{id}`                    Get request by ID

  PUT                     `/meeting-request/cancel/{id}/{organizerId}`   Cancel request
  ----------------------------------------------------------------------------------------------

### Invite Codes

  -----------------------------------------------------------------------------------------------------
  Method                  Endpoint                                              Description
  ----------------------- ----------------------------------------------------- -----------------------
  POST                    `/invite-code/generate/{requestId}/{organizerId}`     Generate invite code

  PUT                     `/invite-code/regenerate/{requestId}/{organizerId}`   Regenerate invite code

  DELETE                  `/invite-code/disable/{requestId}/{organizerId}`      Disable invite code

  GET                     `/invite-code/get/{inviteCode}`                       Find request by invite
                                                                                code
  -----------------------------------------------------------------------------------------------------

### Meeting Request Members

  ------------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                     Description
  ----------------------- ------------------------------------------------------------ -----------------------
  POST                    `/meeting-request-member/invite/{requestId}/{organizerId}`   Invite member by email

  PUT                     `/meeting-request-member/accept/{memberId}/{userId}`         Accept invitation

  PUT                     `/meeting-request-member/reject/{memberId}/{userId}`         Reject invitation

  POST                    `/meeting-request-member/join/{inviteCode}/{userId}`         Join using invite code

  GET                     `/meeting-request-member/accepted/{requestId}`               Get accepted members
  ------------------------------------------------------------------------------------------------------------

### Meetings

  -------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                Description
  ----------------------- ------------------------------------------------------- -----------------------
  GET                     `/meeting/get-all`                                      Get all meetings

  GET                     `/meeting/get/{id}`                                     Get meeting by ID

  POST                    `/meeting/start/{requestId}/{organizerId}`              Start meeting

  PUT                     `/meeting/cancel/{id}/{organizerId}`                    Cancel meeting

  GET                     `/meeting/{meetingId}/missing-locations`                Get participants
                                                                                  missing locations

  GET                     `/meeting/{meetingId}/location-readiness`               Check location
                                                                                  readiness

  POST                    `/meeting/{meetingId}/calculate-center/{organizerId}`   Calculate meeting
                                                                                  center

  GET                     `/meeting/{meetingId}/calculate-recommendations`        Calculate place
                                                                                  recommendations

  POST                    `/meeting/{meetingId}/generate-recommendations`         Generate AI-ranked
                                                                                  recommendations

  PUT                     `/meeting/start-voting/{id}/{organizerId}`              Start voting

  PUT                     `/meeting/confirm/{meetingId}/{organizerId}`            Confirm final place
  -------------------------------------------------------------------------------------------------------

### Participants

  -----------------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                          Description
  ----------------------- ----------------------------------------------------------------- -----------------------
  GET                     `/participant/meeting/{meetingId}`                                Get meeting
                                                                                            participants

  GET                     `/participant/get/{id}`                                           Get participant by ID

  POST                    `/participant/add/{meetingId}/{organizerId}/{userId}`             Add participant

  DELETE                  `/participant/delete/{meetingId}/{organizerId}/{participantId}`   Remove participant

  PUT                     `/participant/location/{participantId}/{userId}`                  Update participant
                                                                                            location
  -----------------------------------------------------------------------------------------------------------------

### Places

  --------------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                       Description
  ----------------------- -------------------------------------------------------------- -----------------------
  GET                     `/place/meeting/{meetingId}`                                   Get meeting voting
                                                                                         places

  GET                     `/place/get/{id}`                                              Get place by ID

  POST                    `/place/meeting/{meetingId}/select/{participantId}/{userId}`   Nominate recommendation
                                                                                         for voting

  DELETE                  `/place/meeting/{meetingId}/remove/{organizerId}/{placeId}`    Remove place from
                                                                                         voting
  --------------------------------------------------------------------------------------------------------------

### Votes

  --------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                 Description
  ----------------------- -------------------------------------------------------- -----------------------
  POST                    `/vote/{meetingId}/{participantId}/{userId}/{placeId}`   Submit vote

  PUT                     `/vote/{meetingId}/{participantId}/{userId}/{placeId}`   Change vote

  GET                     `/vote/place/{placeId}`                                  Get votes for a place

  GET                     `/vote/meeting/{meetingId}/result`                       Get voting result
  --------------------------------------------------------------------------------------------------------

### Participant--Place Routes

  --------------------------------------------------------------------------------------
  Method                  Endpoint                               Description
  ----------------------- -------------------------------------- -----------------------
  GET                     `/route/get/{id}`                      Get route record

  GET                     `/route/place/{placeId}`               Get distances for a
                                                                 place

  GET                     `/route/participant/{participantId}`   Get participant
                                                                 distances
  --------------------------------------------------------------------------------------

------------------------------------------------------------------------

## Authorization Rules

Business-level authorization is enforced in the service layer.

-   Only the meeting organizer can modify or cancel a meeting request.
-   Only the organizer can manage invite codes.
-   Only the organizer can start or cancel a meeting.
-   Only the organizer can add or remove participants after the meeting
    starts.
-   Participants can update only their own location.
-   Participants can nominate places only for meetings they belong to.
-   Participants can submit or change only their own vote.
-   Only the organizer can start voting and confirm the final meeting
    place.

------------------------------------------------------------------------

## External Integrations

### Google Places

Used to discover real places around the calculated meeting center based
on the meeting category.

Supported meeting categories include:

``` text
cafe
restaurant
park
movie_theater
shopping_mall
```

### OpenRouter

Spring AI sends the calculated place candidates to OpenRouter for
ranking. AI receives the existing Google Places results and their
calculated metrics; it does not generate the candidate locations.

### Email

Spring Mail is used for:

-   Welcome emails
-   Meeting invitations
-   Final meeting confirmation
-   Google Maps link to the selected place

------------------------------------------------------------------------

## Environment Variables

Create the required environment variables before running the
application:

``` env
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

GOOGLE_MAPS_API_KEY=your_google_maps_api_key

OPENROUTER_API_KEY=your_openrouter_api_key

MAIL_HOST=your_mail_host
MAIL_PORT=your_mail_port
MAIL_USERNAME=your_mail_username
MAIL_PASSWORD=your_mail_password
```

Do not commit API keys or credentials to the repository.

------------------------------------------------------------------------

## Running the Project

### 1. Clone

``` bash
git clone https://github.com/Inawaf9/capstone2.git
cd capstone2
```

### 2. Create the database

``` sql
CREATE DATABASE meeting_point;
```

### 3. Configure environment variables

Set the database, Google Maps, OpenRouter, and mail credentials required
by `application.properties`.

### 4. Run

macOS / Linux:

``` bash
./mvnw spring-boot:run
```

Windows:

``` bash
mvnw.cmd spring-boot:run
```

The API will be available at:

``` text
http://localhost:8080/api/v1
```

------------------------------------------------------------------------

## Project Structure

``` text
src/main/java/com/nawaf/meetingpoint
├── Client
├── Controller
├── DTO
├── Model
├── Repository
├── Service
└── MeetingPointApplication.java
```

The main separation is:

-   `Controller` --- REST endpoints and HTTP responses
-   `Service` --- business logic and authorization
-   `Repository` --- database access
-   `Model` --- JPA entities
-   `DTO` --- external API and response/request models
-   `Client` --- external API integrations

------------------------------------------------------------------------

## Author

**Nawaf Alghamdi**

Java & Spring Boot Capstone Project\
Tuwaiq Academy

------------------------------------------------------------------------

::: {align="center"}
`<img src="liqaa-logo.png" alt="Liqaa" width="120"/>`{=html}

**Liqaa --- Find the meeting point that works for everyone.**
:::
