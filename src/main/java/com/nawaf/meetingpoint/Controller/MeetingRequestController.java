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
    public ResponseEntity<?> createMeetingRequest(@Valid @RequestBody MeetingRequest meetingRequest, Errors errors) {
        if (errors.hasErrors()) return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));

        int createCase = meetingRequestService.createMeetingRequest(meetingRequest);

        return switch (createCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Organizer not found"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Created new meeting request successfully"));
        };
    }

    @PutMapping("/update/{id}/{userId}")
    public ResponseEntity<?> updateMeetingRequest(@PathVariable Integer id, @PathVariable Integer userId, @Valid @RequestBody MeetingRequest meetingRequest, Errors errors) {
        if (errors.hasErrors()) return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));

        int updateCase = meetingRequestService.updateMeetingRequest(id, userId, meetingRequest);

        return switch (updateCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can update the meeting request"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Updated meeting request successfully"));
        };
    }

    @DeleteMapping("/delete/{id}/{userId}")
    public ResponseEntity<?> deleteMeetingRequest(@PathVariable Integer id, @PathVariable Integer userId) {
        int deleteCase = meetingRequestService.deleteMeetingRequest(id, userId);

        return switch (deleteCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can delete the meeting request"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Deleted meeting request successfully"));
        };
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMeetingRequest(@PathVariable Integer id) {
        MeetingRequest meetingRequest = meetingRequestService.getMeetingRequest(id);

        if (meetingRequest == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));

        return ResponseEntity.status(200).body(meetingRequest);
    }

    @PutMapping("/cancel/{id}/{userId}")
    public ResponseEntity<?> cancelMeetingRequest(@PathVariable Integer id, @PathVariable Integer userId) {
        int cancelCase = meetingRequestService.cancelMeetingRequest(id, userId);

        return switch (cancelCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can cancel the meeting request"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is already cancelled"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Cancelled meeting request successfully"));
        };
    }
}