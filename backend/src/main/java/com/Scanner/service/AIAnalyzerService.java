package com.Scanner.service;

import com.Scanner.model.Vulnerability;

import java.util.List;

/**
 * Interface for AI-powered vulnerability analysis.
 * Implementations provide semantic code analysis using different AI providers.
 */
public interface AIAnalyzerService {
    
    /**
     * Analyze code for vulnerabilities using AI/ML models.
     * 
     * @param code the source code to analyze
     * @param language the programming language
     * @return list of detected vulnerabilities
     */
    List<Vulnerability> analyzeCode(String code, String language);
    
    /**
     * Check if this analyzer is available and properly configured.
     * 
     * @return true if the analyzer can be used
     */
    boolean isAvailable();
}
