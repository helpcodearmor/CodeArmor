package com.Scanner.service;

import com.Scanner.dto.ScanResult;
import com.Scanner.model.Vulnerability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests for ScannerService with mocked AI analyzer.
 */
class ScannerServiceTest {
    
    @Mock
    private AIAnalyzerService aiAnalyzerService;
    
    private StaticCheckService staticCheckService;
    private ScannerService scannerService;
    
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        MockitoAnnotations.openMocks(this);
        staticCheckService = new StaticCheckService();
        scannerService = new ScannerService(staticCheckService, aiAnalyzerService);
        
        // Set test properties
        ReflectionTestUtils.setField(scannerService, "maxFileSizeBytes", 1048576L);
        ReflectionTestUtils.setField(scannerService, "maxLines", 1000);
    }
    
    @Test
    void testScanRawCode_WithStaticAndAI() {
        String code = """
            const password = "admin123";
            document.getElementById('output').innerHTML = userInput;
            """;
        
        // Mock AI response
        List<Vulnerability> aiVulns = List.of(
                Vulnerability.builder()
                        .id("ai-1")
                        .line(2)
                        .type(Vulnerability.VulnerabilityType.XSS)
                        .severity(Vulnerability.Severity.High)
                        .snippet("innerHTML = userInput")
                        .reason("User input assigned to innerHTML without sanitization")
                        .suggestion("Use textContent or sanitize with DOMPurify")
                        .build()
        );
        
        when(aiAnalyzerService.analyzeCode(anyString(), anyString()))
                .thenReturn(aiVulns);
        
        // Perform scan
        ScanResult result = scannerService.scanRawCode(code, "javascript");
        
        // Verify
        assertNotNull(result);
        assertEquals("raw", result.getFilename());
        assertEquals("javascript", result.getLanguage());
        assertFalse(result.getVulnerabilities().isEmpty());
        assertTrue(result.getMeta().getScanTimeMs() > 0);
        
        // Should have vulnerabilities from both static and AI
        assertTrue(result.getVulnerabilities().stream()
                .anyMatch(v -> v.getType() == Vulnerability.VulnerabilityType.SECRET));
    }
    
    @Test
    void testScanRawCode_Deduplication() {
        String code = "document.getElementById('x').innerHTML = y;";
        
        // Mock AI to return same vulnerability as static check
        List<Vulnerability> aiVulns = List.of(
                Vulnerability.builder()
                        .id("ai-1")
                        .line(1)
                        .type(Vulnerability.VulnerabilityType.XSS)
                        .severity(Vulnerability.Severity.High)
                        .snippet("innerHTML = y")
                        .reason("XSS vulnerability")
                        .suggestion("Use textContent")
                        .build()
        );
        
        when(aiAnalyzerService.analyzeCode(anyString(), anyString()))
                .thenReturn(aiVulns);
        
        ScanResult result = scannerService.scanRawCode(code, "javascript");
        
        // Should deduplicate - only one XSS vuln at line 1
        long xssCount = result.getVulnerabilities().stream()
                .filter(v -> v.getType() == Vulnerability.VulnerabilityType.XSS && v.getLine() == 1)
                .count();
        
        assertEquals(1, xssCount, "Should deduplicate vulnerabilities at same line");
    }
    
    @Test
    void testScanRawCode_ExceedsLineLimit() {
        StringBuilder largeCode = new StringBuilder();
        for (int i = 0; i < 1500; i++) {
            largeCode.append("// line ").append(i).append("\n");
        }
        
        assertThrows(IllegalArgumentException.class, () -> {
            scannerService.scanRawCode(largeCode.toString(), "java");
        });
    }
    
    @Test
    void testScanRawCode_ExceedsSizeLimit() {
        // Create code > 200KB
        String largeCode = "a".repeat(250_000);
        
        assertThrows(IllegalArgumentException.class, () -> {
            scannerService.scanRawCode(largeCode, "java");
        });
    }
    
    @Test
    void testLanguageDetection() {
        // Use reflection to call private method
        String language = (String) ReflectionTestUtils.invokeMethod(
                scannerService, "detectLanguage", "Test.java");
        assertEquals("java", language);
        
        language = (String) ReflectionTestUtils.invokeMethod(
                scannerService, "detectLanguage", "app.js");
        assertEquals("javascript", language);
        
        language = (String) ReflectionTestUtils.invokeMethod(
                scannerService, "detectLanguage", "script.py");
        assertEquals("python", language);
        
        language = (String) ReflectionTestUtils.invokeMethod(
                scannerService, "detectLanguage", "unknown.xyz");
        assertEquals("unknown", language);
    }
    
    @Test
    void testScanRawCode_CleanCode() {
        String code = """
            public void safeMethod() {
                System.out.println("Hello World");
            }
            """;
        
        when(aiAnalyzerService.analyzeCode(anyString(), anyString()))
                .thenReturn(List.of());
        
        ScanResult result = scannerService.scanRawCode(code, "java");
        
        assertNotNull(result);
        assertTrue(result.getVulnerabilities().isEmpty(), 
                "Clean code should produce empty vulnerability list");
    }
    
    @Test
    void testMergeVulnerabilities_PreferMoreDetailedReason() {
        String code = "const password = \"test\";";
        
        // Mock AI to return more detailed vulnerability
        List<Vulnerability> aiVulns = List.of(
                Vulnerability.builder()
                        .id("ai-1")
                        .line(1)
                        .type(Vulnerability.VulnerabilityType.SECRET)
                        .severity(Vulnerability.Severity.High)
                        .snippet("password = \"test\"")
                        .reason("Hardcoded password detected. This credential should be stored in " +
                                "environment variables or a secure vault to prevent exposure in source code.")
                        .suggestion("Use process.env.PASSWORD or a secrets manager like AWS Secrets Manager")
                        .build()
        );
        
        when(aiAnalyzerService.analyzeCode(anyString(), anyString()))
                .thenReturn(aiVulns);
        
        ScanResult result = scannerService.scanRawCode(code, "javascript");
        
        // Should prefer AI's more detailed reason
        Vulnerability merged = result.getVulnerabilities().stream()
                .filter(v -> v.getType() == Vulnerability.VulnerabilityType.SECRET)
                .findFirst()
                .orElseThrow();
        
        assertTrue(merged.getReason().length() > 50, 
                "Should use more detailed reason from AI");
    }
}