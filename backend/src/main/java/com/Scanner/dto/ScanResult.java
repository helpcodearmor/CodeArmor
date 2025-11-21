package com.Scanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.Scanner.model.Vulnerability;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vulnerability scan result")
public class ScanResult {
    
    @Schema(description = "Filename or 'raw' for raw code scans", example = "UserController.java")
    private String filename;
    
    @Schema(description = "Programming language", example = "java")
    private String language;
    
    @Schema(description = "List of detected vulnerabilities")
    private List<Vulnerability> vulnerabilities;
    
    @JsonProperty("meta")
    @Schema(description = "Scan metadata")
    private ScanMeta meta;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScanMeta {
        @Schema(description = "Total scan time in milliseconds", example = "1234")
        private long scanTimeMs;
    }
}


