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
        meetingService.startMeeting(requestId, organizerId);

        return ResponseEntity.status(201).body(new ApiResponse("Meeting started successfully"));
    }

    @PutMapping("/cancel/{id}/{organizerId}")
    public ResponseEntity<?> cancelMeeting(@PathVariable Integer id, @PathVariable Integer organizerId) {
        meetingService.cancelMeeting(id, organizerId);

        return ResponseEntity.status(200).body(new ApiResponse("Meeting cancelled successfully"));
    }

    @GetMapping("/{meetingId}/missing-locations")
    public ResponseEntity<?> getMissingLocations(@PathVariable Integer meetingId) {
        return ResponseEntity.status(200).body(meetingService.getMissingLocations(meetingId));
    }

    @GetMapping("/{meetingId}/location-readiness")
    public ResponseEntity<?> locationReadiness(@PathVariable Integer meetingId) {
        Meeting meeting = meetingService.getMeeting(meetingId);

        return ResponseEntity.status(200).body(Map.of("ready", meetingService.isLocationReady(meetingId)));
    }

    @PostMapping("/{meetingId}/calculate-center/{organizerId}")
    public ResponseEntity<?> calculateCenter(@PathVariable Integer meetingId, @PathVariable Integer organizerId) {
        meetingService.calculateCenter(meetingId, organizerId);

        return ResponseEntity.status(200).body(new ApiResponse("Meeting center calculated successfully"));
    }

    @GetMapping("/{meetingId}/calculate-recommendations")
    public ResponseEntity<?> calculatePlaceRecommendations(@PathVariable Integer meetingId) {
        List<RecommendationDTO> recommendations = meetingService.calculatePlaceRecommendations(meetingId);

        return ResponseEntity.status(200).body(recommendations);
    }

    @PostMapping("/{meetingId}/generate-recommendations")
    public ResponseEntity<?> generateRecommendations(@PathVariable Integer meetingId) {
        List<RecommendationDTO> recommendations = meetingService.generateRecommendations(meetingId);

        return ResponseEntity.status(200).body(recommendations);
    }

    @PutMapping("/start-voting/{id}/{organizerId}")
    public ResponseEntity<?> startVoting(@PathVariable Integer id, @PathVariable Integer organizerId) {
        meetingService.startVoting(id, organizerId);

        return ResponseEntity.status(200).body(new ApiResponse("Voting started successfully"));
    }

    @PutMapping("/confirm/{meetingId}/{organizerId}")
    public ResponseEntity<?> confirmMeeting(@PathVariable Integer meetingId, @PathVariable Integer organizerId) {
        meetingService.confirmMeeting(meetingId, organizerId);

        return ResponseEntity.status(200).body(new ApiResponse("Meeting confirmed successfully"));
    }
}