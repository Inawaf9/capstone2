package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Service.InviteCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/invite-code")
@RequiredArgsConstructor
public class InviteCodeController {

    private final InviteCodeService inviteCodeService;

    @PostMapping("/generate/{requestId}/{organizerId}")
    public ResponseEntity<?> generateInviteCode(@PathVariable Integer requestId, @PathVariable Integer organizerId) {
        int generateCase = inviteCodeService.generateInviteCode(requestId, organizerId);

        switch (generateCase) {
            case 1:
                return ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiResponse("Only the organizer can generate the invite code"));
            case 3:
                return ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 4:
                return ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            case 5:
                return ResponseEntity.status(400).body(new ApiResponse("Invite code already exists"));
        }

        MeetingRequest meetingRequest = inviteCodeService.getMeetingRequestByInviteCodeForRequest(requestId);

        return ResponseEntity.status(201).body(Map.of("inviteCode", meetingRequest.getInviteCode()));
    }

    @PutMapping("/regenerate/{requestId}/{organizerId}")
    public ResponseEntity<?> regenerateInviteCode(@PathVariable Integer requestId, @PathVariable Integer organizerId) {
        int regenerateCase = inviteCodeService.regenerateInviteCode(requestId, organizerId);

        switch (regenerateCase) {
            case 1:
                return ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiResponse("Only the organizer can regenerate the invite code"));
            case 3:
                return ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 4:
                return ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            case 5:
                return ResponseEntity.status(400).body(new ApiResponse("Invite code is disabled"));
        }

        MeetingRequest meetingRequest = inviteCodeService.getMeetingRequestByInviteCodeForRequest(requestId);

        return ResponseEntity.status(200).body(Map.of("inviteCode", meetingRequest.getInviteCode()));
    }

    @DeleteMapping("/disable/{requestId}/{organizerId}")
    public ResponseEntity<?> disableInviteCode(@PathVariable Integer requestId, @PathVariable Integer organizerId) {
        int disableCase = inviteCodeService.disableInviteCode(requestId, organizerId);

        return switch (disableCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can disable the invite code"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Invite code is already disabled"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Invite code disabled successfully"));
        };
    }

    @GetMapping("/get/{inviteCode}")
    public ResponseEntity<?> getMeetingRequestByInviteCode(@PathVariable String inviteCode) {
        MeetingRequest meetingRequest = inviteCodeService.getMeetingRequestByInviteCode(inviteCode);

        if (meetingRequest == null) return ResponseEntity.status(400).body(new ApiResponse("Invalid invite code"));
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) return ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) return ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));

        return ResponseEntity.status(200).body(meetingRequest);
    }
}