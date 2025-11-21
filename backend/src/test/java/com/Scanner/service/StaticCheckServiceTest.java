package com.Scanner.service;

import com.Scanner.model.Vulnerability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StaticCheckService.
 * Tests various vulnerability patterns across different languages.
 */
class StaticCheckServiceTest {
    
    private StaticCheckService service;
    
    @BeforeEach
    void setUp() {
        service = new StaticCheckService();
    }
    
    @Test
    void testJavaScriptXSS_innerHTML() {
        String code = """
            function displayMessage(msg) {
                document.getElementById('output').innerHTML = msg;
            }
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "javascript");
        
        assertFalse(vulns.isEmpty(), "Should detect innerHTML XSS");
        Vulnerability vuln = vulns.get(0);
        assertEquals(Vulnerability.VulnerabilityType.XSS, vuln.getType());
        assertEquals(Vulnerability.Severity.High, vuln.getSeverity());
        assertEquals(2, vuln.getLine());
    }
    
    @Test
    void testJavaScriptEval() {
        String code = """
            function runCode(userInput) {
                eval(userInput);
            }
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "javascript");
        
        assertFalse(vulns.isEmpty(), "Should detect eval usage");
        Vulnerability vuln = vulns.get(0);
        assertEquals(Vulnerability.VulnerabilityType.OTHER, vuln.getType());
        assertEquals(Vulnerability.Severity.High, vuln.getSeverity());
        assertTrue(vuln.getReason().contains("eval"));
    }
    
    @Test
    void testJavaSQLInjection() {
        String code = """
            public void getUserById(int userId) {
                String sql = "SELECT * FROM users WHERE id=" + userId;
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
            }
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "java");
        
        assertFalse(vulns.isEmpty(), "Should detect SQL injection");
        
        boolean foundSQLI = vulns.stream()
                .anyMatch(v -> v.getType() == Vulnerability.VulnerabilityType.SQLI);
        assertTrue(foundSQLI, "Should contain SQL injection vulnerability");
        
        Vulnerability sqli = vulns.stream()
                .filter(v -> v.getType() == Vulnerability.VulnerabilityType.SQLI)
                .findFirst()
                .orElseThrow();
        
        assertEquals(Vulnerability.Severity.High, sqli.getSeverity());
        assertTrue(sqli.getSuggestion().toLowerCase().contains("preparedstatement"));
    }
    
    @Test
    void testPythonCommandInjection() {
        String code = """
            import os
            
            def execute_command(cmd):
                os.system(cmd)
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "python");
        
        assertFalse(vulns.isEmpty(), "Should detect os.system usage");
        Vulnerability vuln = vulns.get(0);
        assertEquals(Vulnerability.VulnerabilityType.OTHER, vuln.getType());
        assertEquals(Vulnerability.Severity.High, vuln.getSeverity());
    }
    
    @Test
    void testHardcodedPassword() {
        String code = """
            const config = {
                password: "mySecretPass123"
            };
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "javascript");
        
        assertFalse(vulns.isEmpty(), "Should detect hardcoded password");
        Vulnerability vuln = vulns.get(0);
        assertEquals(Vulnerability.VulnerabilityType.SECRET, vuln.getType());
        assertEquals(Vulnerability.Severity.High, vuln.getSeverity());
    }
    
    @Test
    void testHardcodedAPIKey() {
        String code = """
            String apiKey = "REDACTED_TEST_KEY";
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "java");
        
        assertFalse(vulns.isEmpty(), "Should detect hardcoded API key");
        Vulnerability vuln = vulns.get(0);
        assertEquals(Vulnerability.VulnerabilityType.SECRET, vuln.getType());
    }
    
    @Test
    void testMultipleVulnerabilities() {
        String code = """
            const password = "REDACTED_TEST_PASSWORD";
            
            function displayData(data) {
                document.getElementById('output').innerHTML = data;
            }
            
            function runUserCode(code) {
                eval(code);
            }
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "javascript");
        
        assertTrue(vulns.size() >= 3, "Should detect multiple vulnerabilities");
        
        assertTrue(vulns.stream().anyMatch(v -> v.getType() == Vulnerability.VulnerabilityType.SECRET));
        assertTrue(vulns.stream().anyMatch(v -> v.getType() == Vulnerability.VulnerabilityType.XSS));
        assertTrue(vulns.stream().anyMatch(v -> v.getType() == Vulnerability.VulnerabilityType.OTHER));
    }
    
    @Test
    void testCleanCode() {
        String code = """
            public void getUserById(int userId) {
                String sql = "SELECT * FROM users WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
            }
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "java");
        
        assertTrue(vulns.isEmpty(), "Clean code should have no vulnerabilities");
    }
    
    @Test
    void testLineNumberAccuracy() {
        String code = """
            line 1
            line 2
            const password = "secret";
            line 4
            """;
        
        List<Vulnerability> vulns = service.performStaticChecks(code, "javascript");
        
        assertFalse(vulns.isEmpty());
        assertEquals(3, vulns.get(0).getLine(), "Should report correct line number");
    }
}