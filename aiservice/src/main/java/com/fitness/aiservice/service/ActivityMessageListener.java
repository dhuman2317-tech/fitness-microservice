package com.fitness.aiservice.service;


import com.fitness.aiservice.config.RabbitMqConfig;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Service
@Slf4j
@RequiredArgsConstructor

public class ActivityMessageListener {

    private final ActivityAIService aiService;
    private final RecommendationRepository recommendationRepository;


    private String queueName;

    @RabbitListener(queues = "activity.queue")

    public void processActivity(Activity activity) {
        log.info("Received activity for processing : {}", activity.getId());
        //log.info("Generated Recommendation : {}" , aiService.generateRecommendation(activity));
        Recommendation recommendation = aiService.generateRecommendation(activity);

        recommendationRepository.save(recommendation);

    }
}
