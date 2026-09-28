package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.DTO.Recommendation.SelectPlaceDTO;
import com.nawaf.meetingpoint.Model.Place;
import com.nawaf.meetingpoint.Service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<?> getPlaces(@PathVariable Integer meetingId) {
        return ResponseEntity.status(200).body(placeService.getPlaces(meetingId));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPlace(@PathVariable Integer id) {
        Place place = placeService.getPlace(id);

        if (place == null) return ResponseEntity.status(400).body(new ApiResponse("Place not found"));

        return ResponseEntity.status(200).body(place);
    }

    @DeleteMapping("/remove/{meetingId}/{organizerId}/{placeId}")
    public ResponseEntity<?> removePlace(@PathVariable Integer meetingId, @PathVariable Integer organizerId, @PathVariable Integer placeId) {
        int removeCase = placeService.removePlace(meetingId, organizerId, placeId);

        return switch (removeCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can remove a place"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting must be OPEN"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Place not found"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Place does not belong to this meeting"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Place removed successfully"));
        };
    }

    @PostMapping("/meeting/{meetingId}/select/{participantId}/{userId}")
    public ResponseEntity<?> selectPlaceForVoting(@PathVariable Integer meetingId, @PathVariable Integer participantId, @PathVariable Integer userId, @RequestBody SelectPlaceDTO placeDTO) {
        int selectCase = placeService.selectPlaceForVoting(meetingId, participantId, userId, placeDTO);

        return switch (selectCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Meeting must be OPEN"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Participant not found"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Participant does not belong to this meeting"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("User does not own this participant"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("Place already selected for voting"));
            case 7 -> ResponseEntity.status(400).body(new ApiResponse("Invalid place data"));
            case 8 -> ResponseEntity.status(400).body(new ApiResponse("All participants must provide their location"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Place added to voting successfully"));
        };
    }

    @GetMapping("/google-maps/{placeId}")
    public ResponseEntity<?> getGoogleMapsUrl(@PathVariable Integer placeId) {
        String url = placeService.getGoogleMapsUrl(placeId);

        if (url == null) return ResponseEntity.status(400).body(new ApiResponse("Place not found"));

        return ResponseEntity.status(200).body(Map.of("googleMapsUrl", url));
    }
}