package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Model.ParticipantPlaceRoute;
import com.nawaf.meetingpoint.Service.ParticipantPlaceRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/route")
@RequiredArgsConstructor
public class ParticipantPlaceRouteController {

    private final ParticipantPlaceRouteService participantPlaceRouteService;

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getRoute(@PathVariable Integer id) {
        ParticipantPlaceRoute route = participantPlaceRouteService.getRoute(id);

        if (route == null) return ResponseEntity.status(400).body(new ApiResponse("Route not found"));

        return ResponseEntity.status(200).body(route);
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<?> getRoutesForPlace(@PathVariable Integer placeId) {
        return ResponseEntity.status(200).body(participantPlaceRouteService.getRoutesForPlace(placeId));
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<?> getRoutesForParticipant(@PathVariable Integer participantId) {
        return ResponseEntity.status(200).body(participantPlaceRouteService.getRoutesForParticipant(participantId));
    }
}