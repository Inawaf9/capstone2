package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.DTO.Recommendation.RecommendationDTO;
import com.nawaf.meetingpoint.Model.Meeting;
import com.nawaf.meetingpoint.Service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getMeetings() {
        return ResponseEntity.status(200).body(meetingService.getMeetings());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMeeting(@PathVariable Integer id) {
        Meeting meeting = meetingService.getMeeting(id);

        if (meeting == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));

        return ResponseEntity.status(200).body(meeting);
    }

    @PostMapping("/start/{requestId}/{organizerId}")
    public ResponseEntity<?> startMeeting(@PathVariable Integer requestId, @PathVariable Integer organizerId) {
        int startCase = meetingService.startMeeting(requestId, organizerId);

        return switch (startCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can start the meeting"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Meeting already started"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("No accepted members"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Meeting started successfully"));
        };
    }

    @PutMapping("/cancel/{id}/{organizerId}")
    public ResponseEntity<?> cancelMeeting(@PathVariable Integer id, @PathVariable Integer organizerId) {
        int cancelCase = meetingService.cancelMeeting(id, organizerId);

        return switch (cancelCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can cancel the meeting"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting is already cancelled"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Confirmed meeting cannot be cancelled"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Meeting cancelled successfully"));
        };
    }

    @GetMapping("/{meetingId}/missing-locations")
    public ResponseEntity<?> getMissingLocations(@PathVariable Integer meetingId) {
        Meeting meeting = meetingService.getMeeting(meetingId);

        if (meeting == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));

        return ResponseEntity.status(200).body(meetingService.getMissingLocations(meetingId));
    }

    @GetMapping("/{meetingId}/location-readiness")
    public ResponseEntity<?> locationReadiness(@PathVariable Integer meetingId) {
        Meeting meeting = meetingService.getMeeting(meetingId);

        if (meeting == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));

        return ResponseEntity.status(200).body(Map.of("ready", meetingService.isLocationReady(meetingId)));
    }

    @PostMapping("/{meetingId}/calculate-center/{organizerId}")
    public ResponseEntity<?> calculateCenter(@PathVariable Integer meetingId, @PathVariable Integer organizerId) {
        int centerCase = meetingService.calculateCenter(meetingId, organizerId);

        return switch (centerCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can calculate the meeting center"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting must be OPEN"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Meeting has no participants"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("All participants must provide their location"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Meeting center calculated successfully"));
        };
    }

    @GetMapping("/{meetingId}/calculate-recommendations")
    public ResponseEntity<?> calculatePlaceRecommendations(@PathVariable Integer meetingId) {
        List<RecommendationDTO> recommendations = meetingService.calculatePlaceRecommendations(meetingId);

        if (recommendations == null || recommendations.isEmpty()) return ResponseEntity.status(400).body(new ApiResponse("No places found"));

        return ResponseEntity.status(200).body(recommendations);
    }

    @PostMapping("/{meetingId}/generate-recommendations")
    public ResponseEntity<?> generateRecommendations(@PathVariable Integer meetingId) {
        Meeting meeting = meetingService.getMeeting(meetingId);

        if (meeting == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return ResponseEntity.status(400).body(new ApiResponse("Meeting must be OPEN"));
        if (!meetingService.isLocationReady(meetingId)) return ResponseEntity.status(400).body(new ApiResponse("All participants must provide their location"));
        if (meeting.getCenterLatitude() == null || meeting.getCenterLongitude() == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting center must be calculated first"));

        List<RecommendationDTO> recommendations = meetingService.generateRecommendations(meetingId);

        if (recommendations == null || recommendations.isEmpty()) return ResponseEntity.status(400).body(new ApiResponse("No places found"));

        return ResponseEntity.status(200).body(recommendations);
    }

    @PutMapping("/start-voting/{id}/{organizerId}")
    public ResponseEntity<?> startVoting(@PathVariable Integer id, @PathVariable Integer organizerId) {
        int votingCase = meetingService.startVoting(id, organizerId);

        return switch (votingCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can start voting"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting must be OPEN"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("At least two places are required to start voting"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Voting started successfully"));
        };
    }

    @PutMapping("/confirm/{meetingId}/{organizerId}")
    public ResponseEntity<?> confirmMeeting(@PathVariable Integer meetingId, @PathVariable Integer organizerId) {
        int confirmCase = meetingService.confirmMeeting(meetingId, organizerId);

        return switch (confirmCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can confirm the meeting"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting must be in voting status"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("No places found"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("All participants must vote before confirming the meeting"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Meeting confirmed successfully"));
        };
    }
}