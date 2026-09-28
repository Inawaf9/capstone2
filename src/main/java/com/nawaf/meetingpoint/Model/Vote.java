package com.nawaf.meetingpoint.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id"}))
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Participant id required")
    @Column(name = "participant_id", nullable = false)
    private Integer participantId;

    @NotNull(message = "Place id required")
    @Column(name = "place_id", nullable = false)
    private Integer placeId;
}