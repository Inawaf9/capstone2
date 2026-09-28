package com.nawaf.meetingpoint.Repository;

import com.nawaf.meetingpoint.Model.MeetingRequestMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRequestMemberRepository extends JpaRepository<MeetingRequestMember, Integer> {

    MeetingRequestMember findMeetingRequestMemberById(Integer id);

    MeetingRequestMember findMeetingRequestMemberByMeetingRequestIdAndUserId(Integer meetingRequestId, Integer userId);

    MeetingRequestMember findMeetingRequestMemberByMeetingRequestIdAndEmail(Integer meetingRequestId, String email);

    List<MeetingRequestMember> findMeetingRequestMembersByMeetingRequestIdAndStatus(Integer meetingRequestId, String status);

    List<MeetingRequestMember> findMeetingRequestMembersByEmailAndStatus(String email, String status);
}