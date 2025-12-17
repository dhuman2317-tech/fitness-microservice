import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import org.springframework.beans.factory.annotation.Value;

@Value("${rabbitmq.exchange.name}")
private String exchange;

@Value("${rabbitmq.exchange.key}")
private String routingKey;

public ActivityResponse trackActivity(ActivityRequest activityRequest) {

    boolean isValidUser = userValidationService.validateUser(activityRequest.getUserId()
    if(!isValidUser){
        throw new RuntimeException("Invalid User: " + request.getUserId() )
    }
    Activity activity = Activity.builder()
            .userId(request.getUserId())
            .type(request.getType())
            .duration(request.getDuration)
            .caloriesBurned(request.getCaloriesBurned())
            .startTime(request.getStartTime())
            .additionalMetrics(request.getAdditionalMetrics())
            .build();

    Activity savedActivity = activityRepository.save(activity);

    try{
        rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
    }
    catch (Exception e){
        log.error("Failed to publish activivty to RabbitMq: ", e);
    }

    private ActivityResponse mapToResponse (Activity activity){
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());


    }
}