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
        inviteCodeService.generateInviteCode(requestId, organizerId);

        MeetingRequest meetingRequest = inviteCodeService.getMeetingRequestByInviteCodeForRequest(requestId);

        return ResponseEntity.status(201).body(Map.of("inviteCode", meetingRequest.getInviteCode()));
    }

    @PutMapping("/regenerate/{requestId}/{organizerId}")
    public ResponseEntity<?> regenerateInviteCode(@PathVariable Integer requestId, @PathVariable Integer organizerId) {
        inviteCodeService.regenerateInviteCode(requestId, organizerId);

        MeetingRequest meetingRequest = inviteCodeService.getMeetingRequestByInviteCodeForRequest(requestId);

        return ResponseEntity.status(200).body(Map.of("inviteCode", meetingRequest.getInviteCode()));
    }

    @DeleteMapping("/disable/{requestId}/{organizerId}")
    public ResponseEntity<?> disableInviteCode(@PathVariable Integer requestId, @PathVariable Integer organizerId) {
        inviteCodeService.disableInviteCode(requestId, organizerId);

        return ResponseEntity.status(200).body(new ApiResponse("Invite code disabled successfully"));
    }

    @GetMapping("/get/{inviteCode}")
    public ResponseEntity<?> getMeetingRequestByInviteCode(@PathVariable String inviteCode) {
        MeetingRequest meetingRequest = inviteCodeService.getMeetingRequestByInviteCode(inviteCode);

        return ResponseEntity.status(200).body(meetingRequest);
    }
}