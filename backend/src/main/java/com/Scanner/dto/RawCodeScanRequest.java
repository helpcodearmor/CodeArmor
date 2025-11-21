package com.Scanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for scanning raw code")
public class RawCodeScanRequest {
    
    @NotBlank(message = "Code must not be blank")
    @Size(max = 200000, message = "Code must not exceed 200KB")
    @Schema(
        description = "Source code to scan for vulnerabilities (supports HTML, JavaScript, Java, Python, PHP, etc.)", 
        example = "<!doctype html>\n<html>\n<body>\n  <form method=\"POST\">\n    <input name=\"user\">\n  </form>\n</body>\n</html>",
        required = true
    )
    private String code;
    
    @Schema(
        description = "Programming language (optional - will be auto-detected if not provided or set to 'code')", 
        example = "html",
        allowableValues = {"java", "javascript", "typescript", "python", "php", "ruby", "go", "rust", "c", "cpp", "csharp", "html", "api", "code"},
        defaultValue = "code"
    )
    private String language;
    
    /**
     * Get language with fallback to "code" if null.
     * This ensures backward compatibility and triggers auto-detection.
     */
    public String getLanguage() {
        return language != null ? language : "code";
    }
}