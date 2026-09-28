package com.nawaf.meetingpoint.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MeetingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Organizer id required")
    @Column(nullable = false)
    private Integer organizerId;

    @NotEmpty(message = "Meeting name required")
    @Size(max = 50, message = "Meeting name cannot be more than 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true)
    private String inviteCode;

    @NotEmpty(message = "Category required")
    @Size(max = 30, message = "Category cannot be more than 30 characters")
    @Pattern(regexp = "(?i)^(cafe|restaurant|park|movie_theater|shopping_mall)$", message = "Invalid meeting category")
    @Check(constraints = "category IN ('cafe', 'restaurant', 'park', 'movie_theater', 'shopping_mall')")
    @Column(nullable = false, length = 30)
    private String category;

    @Check(constraints = "status IN ('PENDING', 'READY', 'CANCELLED')")
    @Column(nullable = false, length = 20)
    private String status;

    @NotNull(message = "Meeting time required")
    @FutureOrPresent(message = "Meeting time cannot be in the past")
    @Column(nullable = false)
    private LocalDateTime meetingTime;
}