package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Model.Participant;
import com.nawaf.meetingpoint.Service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<?> getParticipants(@PathVariable Integer meetingId) {
        return ResponseEntity.status(200).body(participantService.getParticipants(meetingId));
    }

    @PostMapping("/add/{meetingId}/{organizerId}/{userId}")
    public ResponseEntity<?> createParticipant(@PathVariable Integer meetingId, @PathVariable Integer organizerId, @PathVariable Integer userId) {
        int addCase = participantService.createParticipant(meetingId, organizerId, userId);

        return switch (addCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Participants can only be added while the meeting is open"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can add participants"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("User not found"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("User is already a participant"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("User has not accepted the meeting invitation"));
            case 7 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Participant added successfully"));
        };
    }

    @DeleteMapping("/delete/{meetingId}/{organizerId}/{participantId}")
    public ResponseEntity<?> removeParticipant(@PathVariable Integer meetingId, @PathVariable Integer organizerId, @PathVariable Integer participantId) {
        int deleteCase = participantService.removeParticipant(meetingId, organizerId, participantId);

        return switch (deleteCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Participants can only be removed while the meeting is open"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can remove participants"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Participant not found"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Participant does not belong to this meeting"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("Organizer cannot be removed from the meeting"));
            case 7 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Participant removed successfully"));
        };
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getParticipant(@PathVariable Integer id) {
        Participant participant = participantService.getParticipant(id);

        if (participant == null) return ResponseEntity.status(400).body(new ApiResponse("Participant not found"));

        return ResponseEntity.status(200).body(participant);
    }

    @PutMapping("/location/{participantId}/{userId}")
    public ResponseEntity<?> updateLocation(@PathVariable Integer participantId, @PathVariable Integer userId, @RequestBody Map<String, Double> body) {
        Double latitude = body.get("latitude");
        Double longitude = body.get("longitude");

        if (latitude == null || longitude == null) return ResponseEntity.status(400).body(new ApiResponse("Latitude and longitude are required"));

        int updateCase = participantService.updateLocation(participantId, userId, latitude, longitude);

        return switch (updateCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Participant not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("You can only update your own location"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Invalid latitude"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Invalid longitude"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("Location can only be updated while the meeting is open"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Location updated successfully"));
        };
    }
}