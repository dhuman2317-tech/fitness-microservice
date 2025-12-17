package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;
    // It is better to reuse the ObjectMapper instance
    private static final ObjectMapper mapper = new ObjectMapper();

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("RESPONSE FROM AI: {}", aiResponse);

        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            // Note: This assumes geminiService.getAnswer returns the raw JSON response from Google.
            // If it returns just the extracted text, skip to parsing 'analysisJson'.
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isMissingNode() || candidates.isEmpty()) {
                throw new IllegalStateException("No candidates found in AI response");
            }

            JsonNode textNode = candidates
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText();

            // Fix: Better Markdown cleaning to handle ```json and ``` wrapping safely
            if (jsonContent.contains("```")) {
                jsonContent = jsonContent.replaceAll("```json", "")
                        .replaceAll("```", "")
                        .trim();
            }

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error processing AI response", e);
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        // Fix: Corrected typos (singeltonList -> singletonList, safet -> safety)
        // and fixed Arrays.asList syntax structure
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType((activity.getType()))
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Data unavailable"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines") :
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific suggestions provided") :
                suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements provided") :
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(" ")
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return """
                You are a professional fitness coach and activity analyst.
                Analyze the following user activity and provide a structured JSON response with:
                - Detailed performance analysis
                - Areas for improvement
                - Personalized next workout suggestions
                - Important safety guidelines

                Activity Details:
                - Type: %s
                - Duration: %d minutes
                - Calories Burned: %d
                - Additional Metrics: %s

                Respond with ONLY valid JSON in this exact structure (no extra text, no markdown):

                {
                    "analysis": {
                        "overall": "string",
                        "pace": "string",
                        "heartRate": "string",
                        "caloriesBurned": "string"
                    },
                    "improvements": [
                        {
                            "area": "string",
                            "recommendation": "string"
                        }
                    ],
                    "suggestions": [
                        {
                            "workout": "string",
                            "description": "string"
                        }
                    ],
                    "safety": [
                        "string"
                    ]
                }
                """.formatted(
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics() != null ? activity.getAdditionalMetrics() : "None provided"
        );
    }
}