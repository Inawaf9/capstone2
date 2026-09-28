package com.nawaf.meetingpoint.Repository;

import com.nawaf.meetingpoint.Model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    Participant findParticipantById(Integer id);

    Participant findParticipantByMeetingIdAndUserId(Integer meetingId, Integer userId);

    List<Participant> findParticipantsByMeetingId(Integer meetingId);

    @Query("select p from Participant p where p.meetingId = ?1 and (p.latitude is null or p.longitude is null)")
    List<Participant> findParticipantsWithMissingLocation(Integer meetingId);

    @Query("select count(p) from Participant p where p.meetingId = ?1")
    long countParticipantsByMeetingId(Integer meetingId);

    @Query("select count(p) from Participant p where p.meetingId = ?1 and (p.latitude is null or p.longitude is null)")
    long countParticipantsWithMissingLocation(Integer meetingId);

    @Query("select avg(p.latitude) from Participant p where p.meetingId = ?1")
    Double findAverageLatitudeByMeetingId(Integer meetingId);

    @Query("select avg(p.longitude) from Participant p where p.meetingId = ?1")
    Double findAverageLongitudeByMeetingId(Integer meetingId);
}