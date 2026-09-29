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
        participantService.createParticipant(meetingId, organizerId, userId);

        return ResponseEntity.status(201).body(new ApiResponse("Participant added successfully"));
    }

    @DeleteMapping("/delete/{meetingId}/{organizerId}/{participantId}")
    public ResponseEntity<?> removeParticipant(@PathVariable Integer meetingId, @PathVariable Integer organizerId, @PathVariable Integer participantId) {
        participantService.removeParticipant(meetingId, organizerId, participantId);

        return ResponseEntity.status(200).body(new ApiResponse("Participant removed successfully"));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getParticipant(@PathVariable Integer id) {
        Participant participant = participantService.getParticipant(id);

        return ResponseEntity.status(200).body(participant);
    }

    @PutMapping("/location/{participantId}/{userId}")
    public ResponseEntity<?> updateLocation(@PathVariable Integer participantId, @PathVariable Integer userId, @RequestBody Map<String, Double> body) {
        Double latitude = body.get("latitude");
        Double longitude = body.get("longitude");

        participantService.updateLocation(participantId, userId, latitude, longitude);

        return ResponseEntity.status(200).body(new ApiResponse("Location updated successfully"));
    }
}