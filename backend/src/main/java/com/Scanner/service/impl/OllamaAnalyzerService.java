package com.Scanner.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Scanner.model.Vulnerability;
import com.Scanner.service.AIAnalyzerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Ollama-based vulnerability analyzer using local LLM models.
 * Requires Ollama to be installed locally: https://ollama.ai
 * 
 * This implementation calls the Ollama CLI to run inference on local models.
 * It's suitable for environments where OpenAI API is not available or desired.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "scanner.ai.provider", havingValue = "ollama")
public class OllamaAnalyzerService implements AIAnalyzerService {
    
    @Value("${scanner.ai.ollama.model:codellama:7b}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<Vulnerability> analyzeCode(String code, String language) {
        if (!isAvailable()) {
            log.warn("Ollama not available, skipping AI analysis");
            return List.of();
        }
        
        try {
            log.debug("Starting Ollama analysis with model: {}", model);
            
            // Build prompt
            String prompt = buildAnalysisPrompt(code, language);
            
            // Write prompt to temp file (Ollama works better with files for long prompts)
            Path tempPromptFile = Files.createTempFile("ollama-prompt-", ".txt");
            try {
                Files.writeString(tempPromptFile, prompt);
                
                // Execute Ollama command
                String response = executeOllama(tempPromptFile.toFile());
                
                log.debug("Received Ollama response, parsing vulnerabilities");
                
                // Parse response
                return parseVulnerabilities(response);
                
            } finally {
                // Clean up temp file
                Files.deleteIfExists(tempPromptFile);
            }
            
        } catch (Exception e) {
            log.error("Error during Ollama analysis", e);
            return List.of();
        }
    }
    
    @Override
    public boolean isAvailable() {
        try {
            // Check if Ollama is installed by running version command
            ProcessBuilder pb = new ProcessBuilder("ollama", "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.debug("Ollama not available: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Execute Ollama CLI with the prompt file.
     * 
     * @param promptFile file containing the prompt
     * @return model response as string
     */
    private String executeOllama(File promptFile) throws Exception {
        // Build Ollama command: ollama run <model> < prompt_file
        ProcessBuilder pb = new ProcessBuilder("ollama", "run", model);
        pb.redirectInput(promptFile);
        
        Process process = pb.start();
        
        // Read output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        // Wait for completion with timeout
        boolean completed = process.waitFor(120, java.util.concurrent.TimeUnit.SECONDS);
        if (!completed) {
            process.destroyForcibly();
            throw new RuntimeException("Ollama execution timed out");
        }
        
        if (process.exitValue() != 0) {
            throw new RuntimeException("Ollama execution failed with exit code: " + process.exitValue());
        }
        
        return output.toString();
    }
    
    /**
     * Build the analysis prompt for Ollama.
     * Similar to OpenAI but may need slightly different formatting.
     */
    private String buildAnalysisPrompt(String code, String language) {
        // Sanitize code
        String sanitizedCode = code.replace("```", "'''");
        
        return String.format("""
            You are a security expert. Analyze the following %s code for vulnerabilities.
            
            IMPORTANT: Respond with ONLY a valid JSON array. No explanation before or after.
            
            Each vulnerability must be a JSON object with these exact fields:
            - id: string (use "ai-1", "ai-2", etc.)
            - line: integer (line number)
            - type: string (one of: XSS, SQLI, CSRF, DI, SECRET, OTHER)
            - severity: string (one of: High, Medium, Low)
            - snippet: string (relevant code fragment)
            - reason: string (why it's a vulnerability)
            - suggestion: string (how to fix it)
            
            If no vulnerabilities found, return: []
            
            Code to analyze:
            ```%s
            %s
            ```
            
            JSON array only:
            """, language, language, sanitizedCode);
    }
    
    /**
     * Parse Ollama response into Vulnerability objects.
     * Ollama responses may be less consistent than OpenAI, so we're more lenient.
     */
    private List<Vulnerability> parseVulnerabilities(String response) {
        try {
            // Clean response: extract JSON array
            String cleaned = response.trim();
            
            // Find JSON array bounds
            int startIdx = cleaned.indexOf('[');
            int endIdx = cleaned.lastIndexOf(']');
            
            if (startIdx == -1 || endIdx == -1) {
                log.warn("No JSON array found in Ollama response");
                return new ArrayList<>();
            }
            
            cleaned = cleaned.substring(startIdx, endIdx + 1);
            
            // Parse JSON
            List<Vulnerability> vulnerabilities = objectMapper.readValue(
                    cleaned,
                    new TypeReference<List<Vulnerability>>() {}
            );
            
            log.info("Ollama analysis found {} vulnerabilities", vulnerabilities.size());
            return vulnerabilities;
            
        } catch (Exception e) {
            log.error("Failed to parse Ollama response: {}", response, e);
            return new ArrayList<>();
        }
    }
}