package com.nawaf.meetingpoint.Model;

import jakarta.persistence.*;
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
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Meeting request id required")
    @Column(nullable = false, unique = true)
    private Integer meetingRequestId;

    private Integer selectedPlaceId;

    @NotEmpty(message = "Status required")
    @Pattern(regexp = "(?i)^(OPEN|VOTING|CONFIRMED|CANCELLED)$", message = "Status must be OPEN, VOTING, CONFIRMED, or CANCELLED")
    @Check(constraints = "status IN ('OPEN', 'VOTING', 'CONFIRMED', 'CANCELLED')")
    @Column(nullable = false, length = 20)
    private String status;

    private Double centerLatitude;

    private Double centerLongitude;

    private String centralArea;
}