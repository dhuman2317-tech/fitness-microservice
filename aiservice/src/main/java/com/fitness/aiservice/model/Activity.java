package com.fitness.aiservice.model;

//import jdk.jfr.DataAmount;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data


public class Activity {
    private String Id;
    private String UserId;
    private String type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
