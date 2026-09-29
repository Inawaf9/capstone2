package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Service.MeetingRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meeting-request")
@RequiredArgsConstructor
public class MeetingRequestController {

    private final MeetingRequestService meetingRequestService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getMeetingRequests() {
        return ResponseEntity.status(200).body(meetingRequestService.getMeetingRequests());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createMeetingRequest(@Valid @RequestBody MeetingRequest meetingRequest) {
        meetingRequestService.createMeetingRequest(meetingRequest);

        return ResponseEntity.status(201).body(new ApiResponse("Created new meeting request successfully"));
    }

    @PutMapping("/update/{id}/{userId}")
    public ResponseEntity<?> updateMeetingRequest(@PathVariable Integer id, @PathVariable Integer userId, @Valid @RequestBody MeetingRequest meetingRequest) {
        meetingRequestService.updateMeetingRequest(id, userId, meetingRequest);

        return ResponseEntity.status(200).body(new ApiResponse("Updated meeting request successfully"));
    }

    @DeleteMapping("/delete/{id}/{userId}")
    public ResponseEntity<?> deleteMeetingRequest(@PathVariable Integer id, @PathVariable Integer userId) {
        meetingRequestService.deleteMeetingRequest(id, userId);

        return ResponseEntity.status(200).body(new ApiResponse("Deleted meeting request successfully"));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMeetingRequest(@PathVariable Integer id) {
        MeetingRequest meetingRequest = meetingRequestService.getMeetingRequest(id);

        return ResponseEntity.status(200).body(meetingRequest);
    }

    @PutMapping("/cancel/{id}/{userId}")
    public ResponseEntity<?> cancelMeetingRequest(@PathVariable Integer id, @PathVariable Integer userId) {
        meetingRequestService.cancelMeetingRequest(id, userId);

        return ResponseEntity.status(200).body(new ApiResponse("Cancelled meeting request successfully"));
    }
}