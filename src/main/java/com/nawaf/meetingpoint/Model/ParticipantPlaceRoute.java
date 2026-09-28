package com.nawaf.meetingpoint.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"participantId", "placeId"}))
public class ParticipantPlaceRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Participant id required")
    @Column(nullable = false)
    private Integer participantId;

    @NotNull(message = "Place id required")
    @Column(nullable = false)
    private Integer placeId;

    @NotNull(message = "Distance required")
    @PositiveOrZero(message = "Distance cannot be negative")
    @Column(nullable = false)
    private Double distanceKm;
}