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
        voteService.vote(meetingId, participantId, userId, placeId);

        return ResponseEntity.status(201).body(new ApiResponse("Vote submitted successfully"));
    }

    @PutMapping("/{meetingId}/{participantId}/{userId}/{placeId}")
    public ResponseEntity<?> changeVote(@PathVariable Integer meetingId, @PathVariable Integer participantId, @PathVariable Integer userId, @PathVariable Integer placeId) {
        voteService.changeVote(meetingId, participantId, userId, placeId);

        return ResponseEntity.status(200).body(new ApiResponse("Vote changed successfully"));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<?> getVotesForPlace(@PathVariable Integer placeId) {
        return ResponseEntity.status(200).body(voteService.getVotesForPlace(placeId));
    }

    @GetMapping("/meeting/{meetingId}/result")
    public ResponseEntity<?> getVotingResult(@PathVariable Integer meetingId) {
        Map<String, Object> result = voteService.getVotingResult(meetingId);

        return ResponseEntity.status(200).body(result);
    }
}