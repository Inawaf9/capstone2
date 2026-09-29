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
        placeService.removePlace(meetingId, organizerId, placeId);

        return ResponseEntity.status(200).body(new ApiResponse("Place removed successfully"));
    }

    @PostMapping("/meeting/{meetingId}/select/{participantId}/{userId}")
    public ResponseEntity<?> selectPlaceForVoting(@PathVariable Integer meetingId, @PathVariable Integer participantId, @PathVariable Integer userId, @RequestBody SelectPlaceDTO placeDTO) {
        placeService.selectPlaceForVoting(meetingId, participantId, userId, placeDTO);

        return ResponseEntity.status(201).body(new ApiResponse("Place added to voting successfully"));
    }

    @GetMapping("/google-maps/{placeId}")
    public ResponseEntity<?> getGoogleMapsUrl(@PathVariable Integer placeId) {
        String url = placeService.getGoogleMapsUrl(placeId);

        if (url == null) return ResponseEntity.status(400).body(new ApiResponse("Place not found"));

        return ResponseEntity.status(200).body(Map.of("googleMapsUrl", url));
    }
}