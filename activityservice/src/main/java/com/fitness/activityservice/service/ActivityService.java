package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service  // ← ADD THIS LINE
@Slf4j
@RequiredArgsConstructor

public class ActivityService {
    public ActivityResponse trackActivity(ActivityRequest request) {
        return null;
    }

    public ActivityResponse getActivityById(String activityId) {
        return null;
    }
}
