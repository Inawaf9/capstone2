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

        meetingRequestMemberService.inviteMember(requestId, organizerId, email);

        return ResponseEntity.status(201).body(new ApiResponse("Invitation sent successfully"));
    }

    @PutMapping("/accept/{memberId}/{userId}")
    public ResponseEntity<?> acceptInvitation(@PathVariable Integer memberId, @PathVariable Integer userId) {
        meetingRequestMemberService.acceptInvitation(memberId, userId);

        return ResponseEntity.status(200).body(new ApiResponse("Invitation accepted successfully"));
    }

    @PutMapping("/reject/{memberId}/{userId}")
    public ResponseEntity<?> rejectInvitation(@PathVariable Integer memberId, @PathVariable Integer userId) {
        meetingRequestMemberService.rejectInvitation(memberId, userId);

        return ResponseEntity.status(200).body(new ApiResponse("Invitation rejected successfully"));
    }

    @PostMapping("/join/{inviteCode}/{userId}")
    public ResponseEntity<?> joinByInviteCode(@PathVariable String inviteCode, @PathVariable Integer userId) {
        meetingRequestMemberService.joinByInviteCode(inviteCode, userId);

        return ResponseEntity.status(201).body(new ApiResponse("Joined meeting successfully"));
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