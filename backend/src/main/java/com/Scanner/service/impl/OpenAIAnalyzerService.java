package com.Scanner.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Scanner.model.Vulnerability;
import com.Scanner.service.AIAnalyzerService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI-based vulnerability analyzer using GPT models.
 * Performs semantic code analysis via OpenAI Chat Completions API.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "scanner.ai.provider", havingValue = "openai", matchIfMissing = true)
public class OpenAIAnalyzerService implements AIAnalyzerService {
    
    @Value("${scanner.ai.openai.api-key}")
    private String apiKey;
    
    @Value("${scanner.ai.openai.model:gpt-4}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<Vulnerability> analyzeCode(String code, String language) {
        if (!isAvailable()) {
            log.warn("OpenAI API key not configured, skipping AI analysis");
            return List.of();
        }
        
        try {
            log.debug("Starting OpenAI analysis with model: {}", model);
            
            // Initialize OpenAI service with timeout
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));
            
            // Build the analysis prompt
            String prompt = buildAnalysisPrompt(code, language);
            
            // Create chat completion request with deterministic temperature
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .temperature(0.0)  // Deterministic output
                    .messages(List.of(
                            new ChatMessage("system", getSystemPrompt()),
                            new ChatMessage("user", prompt)
                    ))
                    .build();
            
            // Call OpenAI API
            String response = service.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
            
            log.debug("Received OpenAI response, parsing vulnerabilities");
            
            // Parse JSON response
            return parseVulnerabilities(response);
            
        } catch (Exception e) {
            log.error("Error during OpenAI analysis", e);
            return List.of();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }
    
    /**
     * Build the system prompt that instructs the AI model.
     * This prompt enforces strict JSON output format.
     */
    private String getSystemPrompt() {
        return """
            
            You are a senior security analyst specializing in static code vulnerability detection.
Your task is to analyze the provided code and return all security vulnerabilities found.

IMPORTANT OUTPUT RULES:
- Respond with ONLY a valid JSON array.
- No text, no markdown, no explanation outside JSON.
- Each item in the array must strictly follow the schema below.
- If no vulnerabilities are found, return: []

            SCHEMA FOR EACH VULNERABILITY OBJECT:
{
  "id": "ai-N",
  "line": <line_number>,
  "type": "XSS|SQLI|CMDI|CSRF|DI|DESERIALIZATION|PATH_TRAVERSAL|SECRET|OTHER",
  "severity": "High|Medium|Low",
  "snippet": "<exact_code_fragment>",
  "reason": "<why this is a vulnerability>",
  "suggestion": "<how to fix it>"
}

DETECTION GUIDELINES:
- XSS: unencoded user input written to HTML, JS, DOM sinks (innerHTML, out.println, etc.)
- SQLI: SQL queries constructed using string concatenation, use of Statement, no parameter binding.
- CMDI: user input passed into Runtime.exec(), ProcessBuilder, system() calls.
- CSRF: unsafe state-changing HTTP endpoints with no CSRF protection.
- DI (Dependency Injection issues): unsafe @ComponentScan, reflection instantiation using user input.
- DESERIALIZATION: ObjectInputStream or library deserialization of untrusted data.
- PATH_TRAVERSAL: user input in file paths without validation.
- SECRET: hardcoded credentials, tokens, API keys.
- OTHER: any additional insecure pattern (hardcoded cryptographic keys, insecure hashing, weak random, etc.)

REQUIRED BEHAVIOR:
- Include every vulnerability found.
- Each vulnerability must have a unique id in sequence: ai-1, ai-2, ai-3, ...
- The "snippet" must be short (max 1–3 lines).
- "reason" and "suggestion" must be concise but clear.
- Line numbers must match the user-provided code exactly.""";
        
        
    }
    
    /**
     * Build the user prompt with the code to analyze.
     * Sanitizes code to prevent prompt injection and wraps in fenced block.
     */
    private String buildAnalysisPrompt(String code, String language) {
        // Sanitize code: escape potential prompt injection attempts
        String sanitizedCode = code.replace("```", "'''");
        
        return String.format("""
            Analyze the following %s code for security vulnerabilities.
            Focus on: SQL injection, XSS, command injection, hardcoded secrets, CSRF, insecure dependencies.
            
            Code to analyze:
            ```%s
            %s
            ```
            
            Return ONLY the JSON array of vulnerabilities. No other text.
            """, language, language, sanitizedCode);
    }
    
    /**
     * Parse the AI response into Vulnerability objects.
     * Handles various response formats and extracts JSON.
     */
    private List<Vulnerability> parseVulnerabilities(String response) {
        try {
            // Clean response: remove markdown code blocks if present
            String cleaned = response.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();
            
            // Parse JSON array
            List<Vulnerability> vulnerabilities = objectMapper.readValue(
                    cleaned, 
                    new TypeReference<List<Vulnerability>>() {}
            );
            
            log.info("OpenAI analysis found {} vulnerabilities", vulnerabilities.size());
            return vulnerabilities;
            
        } catch (Exception e) {
            log.error("Failed to parse OpenAI response: {}", response, e);
            return new ArrayList<>();
        }
    }
}