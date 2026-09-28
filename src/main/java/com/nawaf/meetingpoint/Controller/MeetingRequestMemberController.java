package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Model.MeetingRequestMember;
import com.nawaf.meetingpoint.Service.MeetingRequestMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/meeting-request-member")
@RequiredArgsConstructor
public class MeetingRequestMemberController {

    private final MeetingRequestMemberService meetingRequestMemberService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getMeetingRequestMembers() {
        return ResponseEntity.status(200).body(meetingRequestMemberService.getMeetingRequestMembers());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMeetingRequestMember(@PathVariable Integer id) {
        MeetingRequestMember member = meetingRequestMemberService.getMeetingRequestMember(id);

        if (member == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting request member not found"));

        return ResponseEntity.status(200).body(member);
    }

    @PostMapping("/invite/{requestId}/{organizerId}")
    public ResponseEntity<?> inviteMember(@PathVariable Integer requestId, @PathVariable Integer organizerId, @RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) return ResponseEntity.status(400).body(new ApiResponse("Email is required"));
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) return ResponseEntity.status(400).body(new ApiResponse("Email must be valid"));

        int inviteCase = meetingRequestMemberService.inviteMember(requestId, organizerId, email);

        return switch (inviteCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Only the organizer can invite members"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Email is already invited"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Invitation sent successfully"));
        };
    }

    @PutMapping("/accept/{memberId}/{userId}")
    public ResponseEntity<?> acceptInvitation(@PathVariable Integer memberId, @PathVariable Integer userId) {
        int acceptCase = meetingRequestMemberService.acceptInvitation(memberId, userId);

        return switch (acceptCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Invitation not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Invitation is already accepted"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Invitation is already rejected"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("You must register with the invited email before accepting the invitation"));
            case 7 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            case 8 -> ResponseEntity.status(400).body(new ApiResponse("This invitation does not belong to this user"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Invitation accepted successfully"));
        };
    }

    @PutMapping("/reject/{memberId}/{userId}")
    public ResponseEntity<?> rejectInvitation(@PathVariable Integer memberId, @PathVariable Integer userId) {
        int rejectCase = meetingRequestMemberService.rejectInvitation(memberId, userId);

        return switch (rejectCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Invitation not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request not found"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Invitation is already rejected"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Invitation is already accepted"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            case 7 -> ResponseEntity.status(400).body(new ApiResponse("This invitation does not belong to this user"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Invitation rejected successfully"));
        };
    }

    @PostMapping("/join/{inviteCode}/{userId}")
    public ResponseEntity<?> joinByInviteCode(@PathVariable String inviteCode, @PathVariable Integer userId) {
        int joinCase = meetingRequestMemberService.joinByInviteCode(inviteCode, userId);

        return switch (joinCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Invalid invite code"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request is cancelled"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("User not found"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("User is already a member"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Meeting request already started"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Joined meeting successfully"));
        };
    }

    @GetMapping("/accepted/{requestId}")
    public ResponseEntity<?> getAcceptedMembers(@PathVariable Integer requestId) {
        return ResponseEntity.status(200).body(meetingRequestMemberService.getAcceptedMembers(requestId));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingInvitations(@RequestParam String email) {
        return ResponseEntity.status(200).body(meetingRequestMemberService.getPendingInvitations(email));
    }
}