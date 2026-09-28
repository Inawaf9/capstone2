package com.nawaf.meetingpoint.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MeetingRequestMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Meeting request id required")
    @Column(nullable = false)
    private Integer meetingRequestId;

    @Column
    private Integer userId;

    @NotEmpty(message = "Email required")
    @Email(message = "Email must be valid")
    @Column(nullable = false)
    private String email;

    @NotEmpty(message = "Status required")
    @Pattern(regexp = "(?i)^(PENDING|ACCEPTED|REJECTED)$", message = "Status must be PENDING, ACCEPTED, or REJECTED")
    @Check(constraints = "status IN ('PENDING', 'ACCEPTED', 'REJECTED')")
    @Column(nullable = false, length = 20)
    private String status;
}