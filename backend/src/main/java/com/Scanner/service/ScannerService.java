package com.Scanner.service;

import com.Scanner.dto.ScanResult;
import com.Scanner.model.Vulnerability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main orchestrator service for vulnerability scanning.
 * Coordinates static checks and AI analysis, then merges results.
 * Optimized with smart language detection and validation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScannerService {
    
    private final StaticCheckService staticCheckService;
    private final AIAnalyzerService aiAnalyzerService;
    
    @Value("${scanner.max-file-size-bytes:1048576}")
    private long maxFileSizeBytes;
    
    @Value("${scanner.max-lines:1000}")
    private int maxLines;
    
    // Supported languages for better validation
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of(
        "java", "javascript", "typescript", "python", "php", "ruby", 
        "go", "rust", "c", "cpp", "csharp", "api", "code"
    );
    
    /**
     * Scan an uploaded file for vulnerabilities.
     * 
     * @param file the uploaded file
     * @param language optional language hint
     * @return scan results
     */
    public ScanResult scanFile(MultipartFile file, String language) {
        long startTime = System.currentTimeMillis();
        
        // Validate file
        validateFile(file);
        
        // Read file content
        String code;
        try {
            code = readFileContent(file);
        } catch (Exception e) {
            log.error("Error reading file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
        }
        
        // Smart language detection: use filename first, then content
        if (language == null || language.isBlank() || language.equalsIgnoreCase("code")) {
            // Try filename extension first
            language = detectLanguageFromFilename(file.getOriginalFilename());
            
            // If still unknown, analyze content
            if (language.equals("unknown") || language.equals("code")) {
                language = detectLanguageFromContent(code);
            }
        }
        
        // Validate and normalize language
        language = validateAndNormalizeLanguage(language);
        
        log.info("Detected language: {} for file: {}", language, file.getOriginalFilename());

        // Validate size
        validateCodeSize(code);
        
        // Perform scan
        List<Vulnerability> vulnerabilities = performScan(code, language);
        
        long scanTime = System.currentTimeMillis() - startTime;
        
        return ScanResult.builder()
                .filename(file.getOriginalFilename())
                .language(language)
                .vulnerabilities(vulnerabilities)
                .meta(ScanResult.ScanMeta.builder()
                        .scanTimeMs(scanTime)
                        .build())
                .build();
    }
    
    /**
     * Scan raw code for vulnerabilities.
     * 
     * @param code the source code
     * @param language the programming language
     * @return scan results
     */
    public ScanResult scanRawCode(String code, String language) {
        long startTime = System.currentTimeMillis();

        // Auto detect language if not provided or default
        if (language == null || language.isBlank() || language.equalsIgnoreCase("code")) {
            language = detectLanguageFromContent(code);
        }
        
        // Validate and normalize language
        language = validateAndNormalizeLanguage(language);
        
        log.info("Processing raw code with detected language: {}", language);

        validateCodeSize(code);

        List<Vulnerability> vulnerabilities = performScan(code, language);

        long scanTime = System.currentTimeMillis() - startTime;

        return ScanResult.builder()
                .filename("raw-code")
                .language(language)
                .vulnerabilities(vulnerabilities)
                .meta(ScanResult.ScanMeta.builder()
                        .scanTimeMs(scanTime)
                        .build())
                .build();
    }
    
    /**
     * Perform the actual vulnerability scan by combining static and AI analysis.
     * 
     * @param code source code
     * @param language programming language
     * @return merged and deduplicated list of vulnerabilities
     */
    private List<Vulnerability> performScan(String code, String language) {
        log.info("Starting vulnerability scan for {} code ({} lines)", 
                 language, code.split("\\r?\\n").length);
        
        // Step 1: Run static checks (fast, pattern-based)
        List<Vulnerability> staticVulns = staticCheckService.performStaticChecks(code, language);
        
        // Step 2: Run AI analysis (slower, semantic)
        List<Vulnerability> aiVulns = aiAnalyzerService.analyzeCode(code, language);
        
        // Step 3: Merge and deduplicate results
        List<Vulnerability> merged = mergeVulnerabilities(staticVulns, aiVulns);
        
        log.info("Scan complete. Total vulnerabilities: {} (static: {}, AI: {}, merged: {})",
                merged.size(), staticVulns.size(), aiVulns.size(), merged.size());
        
        return merged;
    }
    
    /**
     * Merge vulnerabilities from static and AI analysis.
     * Deduplicates based on line number and type, prefers more detailed explanations.
     * 
     * @param staticVulns vulnerabilities from static checks
     * @param aiVulns vulnerabilities from AI analysis
     * @return merged and sorted list
     */
    private List<Vulnerability> mergeVulnerabilities(
            List<Vulnerability> staticVulns, 
            List<Vulnerability> aiVulns) {
        
        // Use LinkedHashMap to maintain insertion order while deduplicating
        Map<String, Vulnerability> vulnMap = new LinkedHashMap<>();
        
        // Add static vulnerabilities first (more reliable for pattern-based issues)
        for (Vulnerability vuln : staticVulns) {
            String key = vuln.getLine() + "-" + vuln.getType();
            vulnMap.put(key, vuln);
        }
        
        // Add AI vulnerabilities, merge if at same line+type
        for (Vulnerability vuln : aiVulns) {
            String key = vuln.getLine() + "-" + vuln.getType();
            
            if (!vulnMap.containsKey(key)) {
                // New vulnerability, add it
                vulnMap.put(key, vuln);
            } else {
                // Duplicate found, prefer the one with more detailed explanation
                Vulnerability existing = vulnMap.get(key);
                int existingDetailScore = existing.getReason().length() + existing.getSuggestion().length();
                int newDetailScore = vuln.getReason().length() + vuln.getSuggestion().length();
                
                if (newDetailScore > existingDetailScore) {
                    vulnMap.put(key, vuln);
                    log.debug("Replaced duplicate vulnerability at line {} with more detailed version", vuln.getLine());
                }
            }
        }
        
        // Sort by severity (High > Medium > Low) then by line number
        return vulnMap.values().stream()
                .sorted(Comparator
                        .comparing(Vulnerability::getSeverity, Comparator.reverseOrder())
                        .thenComparingInt(Vulnerability::getLine))
                .collect(Collectors.toList());
    }
    
    /**
     * Validate uploaded file.
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or not provided");
        }
        
        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException(
                    String.format("File size (%d bytes) exceeds limit of %d bytes. Consider chunking large files.", 
                            file.getSize(), maxFileSizeBytes));
        }
        
        // Validate file has content
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename");
        }
        
        log.debug("File validation passed: {} ({} bytes)", filename, file.getSize());
    }
    
    /**
     * Read file content as string with proper encoding.
     */
    private String readFileContent(MultipartFile file) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    
    /**
     * Validate code size to prevent processing extremely large files.
     */
    private void validateCodeSize(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code is empty");
        }
        
        int lineCount = code.split("\\r?\\n").length;
        
        if (lineCount > maxLines) {
            throw new IllegalArgumentException(
                    String.format("Code exceeds maximum line limit of %d lines (actual: %d). " +
                            "Consider chunking by functions/classes.", maxLines, lineCount));
        }
        
        if (code.length() > 200_000) { // ~200KB
            throw new IllegalArgumentException(
                    String.format("Code exceeds size limit of 200KB (actual: %d KB). Consider chunking.",
                            code.length() / 1024));
        }
        
        log.debug("Code size validation passed: {} lines, {} bytes", lineCount, code.length());
    }
    
    /**
     * Detect programming language from filename extension.
     * More comprehensive than previous version.
     */
    private String detectLanguageFromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unknown";
        }
        
        String lower = filename.toLowerCase();
        
        // Java
        if (lower.endsWith(".java")) return "java";
        
        // JavaScript/TypeScript
        if (lower.endsWith(".js")) return "javascript";
        if (lower.endsWith(".jsx")) return "javascript";
        if (lower.endsWith(".mjs")) return "javascript";
        if (lower.endsWith(".ts")) return "typescript";
        if (lower.endsWith(".tsx")) return "typescript";
        
        // Python
        if (lower.endsWith(".py")) return "python";
        if (lower.endsWith(".pyw")) return "python";
        
        // PHP
        if (lower.endsWith(".php")) return "php";
        if (lower.endsWith(".phtml")) return "php";
        
        // Ruby
        if (lower.endsWith(".rb")) return "ruby";
        
        // Go
        if (lower.endsWith(".go")) return "go";
        
        // Rust
        if (lower.endsWith(".rs")) return "rust";
        
        // C/C++
        if (lower.endsWith(".c")) return "c";
        if (lower.endsWith(".h")) return "c";
        if (lower.endsWith(".cpp")) return "cpp";
        if (lower.endsWith(".cc")) return "cpp";
        if (lower.endsWith(".cxx")) return "cpp";
        if (lower.endsWith(".hpp")) return "cpp";
        if (lower.endsWith(".hxx")) return "cpp";
        
        // C#
        if (lower.endsWith(".cs")) return "csharp";
        
        // Web files
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "javascript";
        if (lower.endsWith(".vue")) return "javascript";
        
        return "unknown";
    }
    
    /**
     * Detect programming language from code content.
     * Enhanced with better pattern matching and case-insensitive detection.
     */
    private String detectLanguageFromContent(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "code";
        }

        String trimmed = input.trim();
        String lower = input.toLowerCase(); // Case-insensitive matching

        // Detect API / URL payload (check first as it's most specific)
        if (trimmed.matches("^(https?://|www\\.).*")) {
            log.debug("Detected API/URL input");
            return "api";
        }

        // Java detection (strongest indicators first)
        if (lower.contains("public class") || 
            lower.contains("private class") ||
            lower.contains("system.out.print") ||
            lower.contains("@override") ||
            lower.contains("executequery") ||
            lower.matches(".*\\bimport\\s+java\\..*")) {
            return "java";
        }

        // Python detection
        if (lower.matches(".*\\bdef\\s+\\w+\\s*\\(.*") ||
            lower.contains("import ") && lower.contains("from ") ||
            lower.contains("print(") ||
            lower.contains("if __name__") ||
            lower.contains("self.") ||
            lower.contains("def ")) {
            return "python";
        }

        // JavaScript detection (most common patterns)
        if (lower.contains("const ") || 
            lower.contains("let ") ||
            lower.contains("var ") ||
            lower.contains("function ") ||
            lower.contains("console.log") ||
            lower.contains("=>") ||
            lower.contains("innerhtml") ||
            lower.contains("document.") ||
            lower.contains("window.") ||
            lower.contains("require(") ||
            lower.contains("module.exports")) {
            return "javascript";
        }

        // TypeScript detection
        if (lower.contains("interface ") ||
            lower.contains(": string") ||
            lower.contains(": number") ||
            lower.contains(": boolean") ||
            lower.contains("type ") && lower.contains(" = ")) {
            return "typescript";
        }

        // PHP detection
        if (lower.contains("<?php") ||
            lower.contains("<?=") ||
            lower.contains("mysql_query") ||
            lower.contains("$_get") ||
            lower.contains("$_post") ||
            lower.contains("->") && lower.contains("$")) {
            return "php";
        }

        // C/C++ detection
        if (lower.contains("#include") ||
            lower.contains("int main(") ||
            lower.contains("printf(") ||
            lower.contains("cout <<") ||
            lower.contains("std::")) {
            return lower.contains("cout") || lower.contains("std::") ? "cpp" : "c";
        }

        // Go detection
        if (lower.contains("package main") ||
            lower.contains("func main()") ||
            lower.contains("import (") ||
            lower.contains("fmt.print")) {
            return "go";
        }

        // Ruby detection
        if (lower.contains("def ") && lower.contains("end") ||
            lower.contains("puts ") ||
            lower.contains("require '")) {
            return "ruby";
        }

        // Rust detection
        if (lower.contains("fn main()") ||
            lower.contains("let mut ") ||
            lower.contains("println!")) {
            return "rust";
        }

        // Default to generic code if no specific language detected
        log.debug("Could not detect specific language, defaulting to 'code'");
        return "code";
    }
    
    /**
     * Validate and normalize language name.
     * Ensures consistent language naming and handles aliases.
     */
    private String validateAndNormalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "code";
        }
        
        String normalized = language.toLowerCase().trim();
        
        // Handle common aliases
        Map<String, String> aliases = Map.of(
            "js", "javascript",
            "ts", "typescript",
            "py", "python",
            "c++", "cpp",
            "c#", "csharp",
            "cs", "csharp"
        );
        
        normalized = aliases.getOrDefault(normalized, normalized);
        
        // Validate against supported languages
        if (!SUPPORTED_LANGUAGES.contains(normalized)) {
            log.warn("Unsupported language '{}', defaulting to 'code'", language);
            return "code";
        }
        
        return normalized;
    }
}