package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{meetingId}/{participantId}/{userId}/{placeId}")
    public ResponseEntity<?> vote(@PathVariable Integer meetingId, @PathVariable Integer participantId, @PathVariable Integer userId, @PathVariable Integer placeId) {
        int voteCase = voteService.vote(meetingId, participantId, userId, placeId);

        return switch (voteCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Meeting is not in voting status"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Participant not found"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Participant does not belong to this meeting"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("You can only vote using your own participant"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("Place not found"));
            case 7 -> ResponseEntity.status(400).body(new ApiResponse("Place does not belong to this meeting"));
            case 8 -> ResponseEntity.status(400).body(new ApiResponse("Participant already voted"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Vote submitted successfully"));
        };
    }

    @PutMapping("/{meetingId}/{participantId}/{userId}/{placeId}")
    public ResponseEntity<?> changeVote(@PathVariable Integer meetingId, @PathVariable Integer participantId, @PathVariable Integer userId, @PathVariable Integer placeId) {
        int voteCase = voteService.changeVote(meetingId, participantId, userId, placeId);

        return switch (voteCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Meeting is not in voting status"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Participant not found"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Participant does not belong to this meeting"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("You can only change your own vote"));
            case 6 -> ResponseEntity.status(400).body(new ApiResponse("Place not found"));
            case 7 -> ResponseEntity.status(400).body(new ApiResponse("Place does not belong to this meeting"));
            case 8 -> ResponseEntity.status(400).body(new ApiResponse("Vote not found"));
            case 9 -> ResponseEntity.status(400).body(new ApiResponse("Participant already voted for this place"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Vote changed successfully"));
        };
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<?> getVotesForPlace(@PathVariable Integer placeId) {
        return ResponseEntity.status(200).body(voteService.getVotesForPlace(placeId));
    }

    @GetMapping("/meeting/{meetingId}/result")
    public ResponseEntity<?> getVotingResult(@PathVariable Integer meetingId) {
        Map<String, Object> result = voteService.getVotingResult(meetingId);

        if (result == null) return ResponseEntity.status(400).body(new ApiResponse("Meeting not found"));

        return ResponseEntity.status(200).body(result);
    }
}