package com.Scanner.controller;

import com.Scanner.dto.RawCodeScanRequest;
import com.Scanner.dto.ScanResult;
import com.Scanner.service.ScannerService;
import com.Scanner.util.RateLimitInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for vulnerability scanning endpoints.
 * Handles file uploads and raw code submissions for security analysis.
 */
@Slf4j
@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
@Tag(name = "Vulnerability Scanner", description = "Endpoints for scanning code vulnerabilities (SQL Injection, XSS, CSRF, etc.)")
public class ScannerController {
    
    private final ScannerService scannerService;
    private final RateLimitInterceptor rateLimitInterceptor;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Check if the scanner service is running"
    )
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("⚡ Code Vulnerability Scanner is running!");
    }
    
    /**
     * Scan uploaded file for vulnerabilities.
     * Supports multiple programming languages with auto-detection.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Scan uploaded file for vulnerabilities",
        description = "Upload a code file for vulnerability analysis. Language is auto-detected from filename or content.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Scan completed successfully",
                content = @Content(schema = @Schema(implementation = ScanResult.class))
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Invalid file or request parameters"
            ),
            @ApiResponse(
                responseCode = "413", 
                description = "File too large (exceeds size limit)"
            ),
            @ApiResponse(
                responseCode = "429", 
                description = "Rate limit exceeded - too many requests"
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "Internal server error during scanning"
            )
        }
    )
    public ResponseEntity<ScanResult> scanFile(
            @Parameter(description = "Code file to scan (supports .java, .js, .py, .php, etc.)")
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Programming language (optional, auto-detected if not provided)")
            @RequestParam(value = "language", required = false) String language,
            
            HttpServletRequest request) {
        
        try {
            // Check rate limit first
            rateLimitInterceptor.checkRateLimit(request);
            
            log.info("📁 File scan request: filename='{}', size={} bytes, language='{}'", 
                     file.getOriginalFilename(), 
                     file.getSize(), 
                     language != null ? language : "auto-detect");
            
            // Delegate to service layer (handles language detection)
            ScanResult result = scannerService.scanFile(file, language);
            
            log.info("✅ File scan completed: {} vulnerabilities found", 
                     result.getVulnerabilities().size());
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("❌ Invalid file scan request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Error during file scan: {}", e.getMessage(), e);
            throw new RuntimeException("File scan failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Scan raw code for vulnerabilities.
     * Accepts code as JSON payload with optional language specification.
     */
    @PostMapping(value = "/raw", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Scan raw code for vulnerabilities",
        description = "Submit raw code as JSON for vulnerability scanning. Language is auto-detected from code patterns.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Scan completed successfully",
                content = @Content(schema = @Schema(implementation = ScanResult.class))
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Invalid request body or code"
            ),
            @ApiResponse(
                responseCode = "413", 
                description = "Code too large (exceeds size limit)"
            ),
            @ApiResponse(
                responseCode = "429", 
                description = "Rate limit exceeded - too many requests"
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "Internal server error during scanning"
            )
        }
    )
    public ResponseEntity<ScanResult> scanRawCode(
            @Valid @RequestBody RawCodeScanRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Check rate limit first
            rateLimitInterceptor.checkRateLimit(httpRequest);
            
            log.info("📝 Raw code scan request: language='{}', codeLength={} chars", 
                     request.getLanguage() != null ? request.getLanguage() : "auto-detect",
                     request.getCode().length());
            
            // Delegate to service layer (handles language detection and validation)
            ScanResult result = scannerService.scanRawCode(
                request.getCode(), 
                request.getLanguage()
            );
            
            log.info("✅ Raw code scan completed: {} vulnerabilities found in {} code", 
                     result.getVulnerabilities().size(),
                     result.getLanguage());
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("❌ Invalid raw code scan request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Error during raw code scan: {}", e.getMessage(), e);
            throw new RuntimeException("Raw code scan failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get supported languages for scanning.
     */
    @GetMapping("/supported-languages")
    @Operation(
        summary = "Get supported programming languages",
        description = "Returns a list of programming languages supported by the scanner"
    )
    public ResponseEntity<SupportedLanguagesResponse> getSupportedLanguages() {
        SupportedLanguagesResponse response = new SupportedLanguagesResponse(
            java.util.List.of(
                "java", "javascript", "typescript", "python", "php", 
                "ruby", "go", "rust", "c", "cpp", "csharp", "api"
            ),
            "Language auto-detection is available for all supported languages"
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * DTO for supported languages response
     */
    public record SupportedLanguagesResponse(
        java.util.List<String> languages,
        String note
    ) {}
    
    /**
     * Global exception handler for controller
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Validation error: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("validation_error", e.getMessage()));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Runtime error: {}", e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("internal_error", "An error occurred during scanning"));
    }
    
    /**
     * DTO for error responses
     */
    public record ErrorResponse(
        String error,
        String message
    ) {}
}