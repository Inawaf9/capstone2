package com.nawaf.meetingpoint.Service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FairnessService {

    public double calculateAverageDistance(List<Double> distances) {
        if (distances.isEmpty()) return 0;

        double totalDistance = 0;

        for (Double distance : distances) {
            totalDistance += distance;
        }

        return totalDistance / distances.size();
    }

    public double calculateMaxDistance(List<Double> distances) {
        if (distances.isEmpty()) return 0;

        double maxDistance = distances.getFirst();

        for (Double distance : distances) {
            if (distance > maxDistance) maxDistance = distance;
        }

        return maxDistance;
    }

    public double calculateFairnessScore(List<Double> distances) {
        if (distances.isEmpty()) return 0;

        double maxDistance = distances.getFirst();
        double minDistance = distances.getFirst();

        for (Double distance : distances) {
            if (distance > maxDistance) maxDistance = distance;
            if (distance < minDistance) minDistance = distance;
        }

        if (maxDistance == 0) return 100;

        return 100 - (((maxDistance - minDistance) / maxDistance) * 100);
    }
}