package com.nawaf.meetingpoint.Repository;

import com.nawaf.meetingpoint.Model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    Vote findVoteByParticipantId(Integer participantId);

    List<Vote> findVotesByPlaceId(Integer placeId);

    long countVotesByPlaceId(Integer placeId);
}