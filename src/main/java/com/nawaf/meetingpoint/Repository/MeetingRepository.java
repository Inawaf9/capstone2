package com.nawaf.meetingpoint.Repository;

import com.nawaf.meetingpoint.Model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

    Meeting findMeetingById(Integer id);

    Meeting findMeetingByMeetingRequestId(Integer meetingRequestId);
}