package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Api.ApiException;
import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Model.MeetingRequestMember;
import com.nawaf.meetingpoint.Model.User;
import com.nawaf.meetingpoint.Repository.MeetingRequestMemberRepository;
import com.nawaf.meetingpoint.Repository.MeetingRequestRepository;
import com.nawaf.meetingpoint.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingRequestMemberService {

    private final MeetingRequestMemberRepository meetingRequestMemberRepository;
    private final MeetingRequestRepository meetingRequestRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public List<MeetingRequestMember> getMeetingRequestMembers() {
        return meetingRequestMemberRepository.findAll();
    }

    public MeetingRequestMember getMeetingRequestMember(Integer id) {
        return meetingRequestMemberRepository.findMeetingRequestMemberById(id);
    }

    // 0 = Invitation sent successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is canceled
    // 4 = Email is already invited
    // 5 = Meeting request already started
    @Transactional
    public void inviteMember(Integer requestId, Integer organizerId, String email) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(organizerId)) throw new ApiException("User is not the organizer");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");

        email = email.trim().toLowerCase();

        MeetingRequestMember oldMember = meetingRequestMemberRepository.findMeetingRequestMemberByMeetingRequestIdAndEmail(requestId, email);

        if (oldMember != null) throw new ApiException("Email is already invited");

        User user = userRepository.findUserByEmail(email);

        MeetingRequestMember member = new MeetingRequestMember();

        member.setMeetingRequestId(requestId);
        member.setEmail(email);
        member.setStatus("PENDING");

        if (user != null) member.setUserId(user.getId());

        meetingRequestMemberRepository.save(member);

        User organizer = userRepository.findUserById(meetingRequest.getOrganizerId());

        String organizerName = "Meeting Point User";

        if (organizer != null) organizerName = organizer.getName();

        String invitationUrl = "http://localhost:8080/invitation/" + member.getId();

        emailService.sendMeetingInvitation(email, organizerName, meetingRequest.getName(), meetingRequest.getMeetingTime().toString(), invitationUrl);
    }

    // 0 = Invitation accepted successfully
    // 1 = Invitation not found
    // 2 = Meeting request not found
    // 3 = Meeting request is canceled
    // 4 = Invitation is already accepted
    // 5 = Invitation is already rejected
    // 6 = User is not registered with the invited email
    // 7 = Meeting request already started
    // 8 = Invitation does not belong to this user
    public void acceptInvitation(Integer memberId, Integer userId) {
        MeetingRequestMember member = meetingRequestMemberRepository.findMeetingRequestMemberById(memberId);

        if (member == null) throw new ApiException("Invitation not found");

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(member.getMeetingRequestId());

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");
        if (member.getStatus().equalsIgnoreCase("ACCEPTED")) throw new ApiException("Invitation is already accepted");
        if (member.getStatus().equalsIgnoreCase("REJECTED")) throw new ApiException("Invitation is already rejected");

        User user = userRepository.findUserById(userId);

        if (user == null) throw new ApiException("User is not registered with the invited email");
        if (!user.getEmail().equalsIgnoreCase(member.getEmail())) throw new ApiException("Invitation does not belong to this user");

        member.setUserId(user.getId());
        member.setStatus("ACCEPTED");

        meetingRequestMemberRepository.save(member);
    }

    // 0 = Invitation rejected successfully
    // 1 = Invitation not found
    // 2 = Meeting request not found
    // 3 = Meeting request is canceled
    // 4 = Invitation is already rejected
    // 5 = Invitation is already accepted
    // 6 = Meeting request already started
    // 7 = Invitation does not belong to this user
    public void rejectInvitation(Integer memberId, Integer userId) {
        MeetingRequestMember member = meetingRequestMemberRepository.findMeetingRequestMemberById(memberId);

        if (member == null) throw new ApiException("Invitation not found");

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(member.getMeetingRequestId());

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");
        if (member.getStatus().equalsIgnoreCase("REJECTED")) throw new ApiException("Invitation is already accepted");
        if (member.getStatus().equalsIgnoreCase("ACCEPTED")) throw new ApiException("Invitation is already rejected");

        User user = userRepository.findUserById(userId);

        if (user == null || !user.getEmail().equalsIgnoreCase(member.getEmail())) throw new ApiException("User is not registered with the invited email");

        member.setUserId(user.getId());
        member.setStatus("REJECTED");

        meetingRequestMemberRepository.save(member);
    }

    // 0 = Joined meeting request successfully
    // 1 = Invalid invite code
    // 2 = Meeting request is canceled
    // 3 = User not found
    // 4 = User is already a member
    // 5 = Meeting request already started
    public void joinByInviteCode(String inviteCode, Integer userId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestByInviteCode(inviteCode);

        if (meetingRequest == null) throw new ApiException("Invalid invite code");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");

        User user = userRepository.findUserById(userId);

        if (user == null) throw new ApiException("User not found");

        MeetingRequestMember member = meetingRequestMemberRepository.findMeetingRequestMemberByMeetingRequestIdAndEmail(meetingRequest.getId(), user.getEmail());

        if (member != null) {
            if (member.getStatus().equalsIgnoreCase("PENDING")) {
                member.setUserId(userId);
                member.setStatus("ACCEPTED");
                meetingRequestMemberRepository.save(member);
            }

            throw new ApiException("User is already a member");
        }

        MeetingRequestMember newMember = new MeetingRequestMember();

        newMember.setMeetingRequestId(meetingRequest.getId());
        newMember.setUserId(userId);
        newMember.setEmail(user.getEmail().trim().toLowerCase());
        newMember.setStatus("ACCEPTED");

        meetingRequestMemberRepository.save(newMember);
    }

    public List<MeetingRequestMember> getAcceptedMembers(Integer requestId) {
        return meetingRequestMemberRepository.findMeetingRequestMembersByMeetingRequestIdAndStatus(requestId, "ACCEPTED");
    }

    public List<MeetingRequestMember> getPendingInvitations(String email) {
        return meetingRequestMemberRepository.findMeetingRequestMembersByEmailAndStatus(email.trim().toLowerCase(), "PENDING");
    }
}