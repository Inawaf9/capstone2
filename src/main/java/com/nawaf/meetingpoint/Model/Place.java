package com.nawaf.meetingpoint.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"meetingId", "googlePlaceId"}))
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Meeting id required")
    @Column(nullable = false)
    private Integer meetingId;

    @NotEmpty(message = "Google place id required")
    @Column(nullable = false)
    private String googlePlaceId;

    @NotEmpty(message = "Place name required")
    @Size(max = 100, message = "Place name cannot be more than 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotEmpty(message = "Address required")
    @Size(max = 255, message = "Address cannot be more than 255 characters")
    @Column(nullable = false)
    private String address;

    @NotNull(message = "Latitude required")
    @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Latitude must be at most 90")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude required")
    @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Longitude must be at most 180")
    @Column(nullable = false)
    private Double longitude;

    @DecimalMin(value = "0.0", message = "Rating cannot be less than 0")
    @DecimalMax(value = "5.0", message = "Rating cannot be more than 5")
    @Column
    private Double rating;

    @DecimalMin(value = "0.0", message = "Fairness score cannot be less than 0")
    @DecimalMax(value = "100.0", message = "Fairness score cannot be more than 100")
    @Column
    private Double fairnessScore;

    @DecimalMin(value = "0.0", message = "AI score cannot be less than 0")
    @DecimalMax(value = "100.0", message = "AI score cannot be more than 100")
    @Column
    private Double aiScore;

    @Size(max = 500, message = "AI reason cannot be more than 500 characters")
    @Column(length = 500)
    private String aiReason;
}