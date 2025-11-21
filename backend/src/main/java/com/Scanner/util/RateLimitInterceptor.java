// package com.Scanner.util;

// import io.github.bucket4j.Bandwidth;
// import io.github.bucket4j.Bucket;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ResponseStatusException;

// import java.time.Duration;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// /**
//  * IP-based rate limiting using token bucket algorithm.
//  * Prevents abuse by limiting requests per IP address.
//  */
// @Slf4j
// @Component
// public class RateLimitInterceptor {
    
//     @Value("${scanner.rate-limit.requests-per-minute:10}")
//     private int requestsPerMinute;
    
//     // Store buckets per IP address
//     private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
//     /**
//      * Check if the request should be rate-limited.
//      * Throws exception if rate limit is exceeded.
//      * 
//      * @param request the HTTP request
//      */
//     public void checkRateLimit(HttpServletRequest request) {
//         String clientIp = getClientIp(request);
//         Bucket bucket = getBucket(clientIp);
        
//         if (!bucket.tryConsume(1)) {
//             log.warn("Rate limit exceeded for IP: {}", clientIp);
//             throw new ResponseStatusException(
//                     HttpStatus.TOO_MANY_REQUESTS,
//                     "Rate limit exceeded. Maximum " + requestsPerMinute + " requests per minute."
//             );
//         }
        
//         log.debug("Rate limit check passed for IP: {}", clientIp);
//     }
    
//     /**
//      * Get or create a bucket for the given IP address.
//      * Uses token bucket algorithm with refill every minute.
//      * 
//      * @param ip client IP address
//      * @return rate limit bucket
//      */
//     private Bucket getBucket(String ip) {
//         return buckets.computeIfAbsent(ip, k -> {
//             // Create a bucket that refills to requestsPerMinute tokens every minute
//             Bandwidth limit = Bandwidth.builder()
//                 .capacity(requestsPerMinute)
//                 .refillIntervally(requestsPerMinute, Duration.ofMinutes(1))
//                 .build();
//              return Bucket.builder()
//                 .addLimit(limit)
//                 .build();
//         });
//     }
    
//     /**
//      * Extract client IP address from request.
//      * Handles common proxy headers.
//      * 
//      * @param request HTTP request
//      * @return client IP address
//      */
//     private String getClientIp(HttpServletRequest request) {
//         // Check common proxy headers
//         String ip = request.getHeader("X-Forwarded-For");
//         if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//             ip = request.getHeader("X-Real-IP");
//         }
//         if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//             ip = request.getRemoteAddr();
//         }
        
//         // X-Forwarded-For can contain multiple IPs, take the first one
//         if (ip != null && ip.contains(",")) {
//             ip = ip.split(",")[0].trim();
//         }
        
//         return ip != null ? ip : "unknown";
//     }
// }








package com.Scanner.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP-based rate limiting using token bucket algorithm.
 * Prevents abuse by limiting requests per IP address.
 * Includes automatic cleanup of inactive IP buckets to prevent memory leaks.
 */
@Slf4j
@Component
public class RateLimitInterceptor {
    
    @Value("${scanner.rate-limit.requests-per-minute:10}")
    private int requestsPerMinute;
    
    @Value("${scanner.rate-limit.cleanup-after-minutes:60}")
    private int cleanupAfterMinutes;
    
    // Store buckets per IP address with last access time
    private final Map<String, BucketWithTimestamp> buckets = new ConcurrentHashMap<>();
    
    /**
     * Check if the request should be rate-limited.
     * Throws exception if rate limit is exceeded.
     * 
     * @param request the HTTP request
     * @throws ResponseStatusException if rate limit exceeded
     */
    public void checkRateLimit(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        BucketWithTimestamp bucketData = getBucketWithTimestamp(clientIp);
        
        if (!bucketData.bucket().tryConsume(1)) {
            log.warn("⚠️ Rate limit exceeded for IP: {} (limit: {} requests/min)", 
                     clientIp, requestsPerMinute);
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    String.format("Rate limit exceeded. Maximum %d requests per minute allowed. Please try again later.", 
                                requestsPerMinute)
            );
        }
        
        log.debug("✅ Rate limit check passed for IP: {} (Active IPs: {})", 
                  clientIp, buckets.size());
    }
    
    /**
     * Get or create a bucket for the given IP address.
     * Uses token bucket algorithm with refill every minute.
     * Updates the last access timestamp.
     * 
     * @param ip client IP address
     * @return bucket with timestamp
     */
    private BucketWithTimestamp getBucketWithTimestamp(String ip) {
        return buckets.compute(ip, (k, existing) -> {
            if (existing != null) {
                // Update timestamp for existing bucket
                return new BucketWithTimestamp(existing.bucket(), Instant.now());
            } else {
                // Create new bucket
                Bandwidth limit = Bandwidth.builder()
                    .capacity(requestsPerMinute)
                    .refillIntervally(requestsPerMinute, Duration.ofMinutes(1))
                    .build();
                
                Bucket newBucket = Bucket.builder()
                    .addLimit(limit)
                    .build();
                
                log.debug("🆕 Created new rate limit bucket for IP: {}", ip);
                return new BucketWithTimestamp(newBucket, Instant.now());
            }
        });
    }
    
    /**
     * Scheduled task to cleanup inactive IP buckets.
     * Runs every hour to prevent memory leaks from accumulating IP addresses.
     * Removes buckets that haven't been accessed in the configured time period.
     */
    @Scheduled(fixedRateString = "${scanner.rate-limit.cleanup-interval-ms:3600000}") // Default: 1 hour
    public void cleanupInactiveBuckets() {
        Instant cutoffTime = Instant.now().minus(Duration.ofMinutes(cleanupAfterMinutes));
        int initialSize = buckets.size();
        
        buckets.entrySet().removeIf(entry -> {
            boolean isInactive = entry.getValue().lastAccess().isBefore(cutoffTime);
            if (isInactive) {
                log.debug("🗑️ Removing inactive bucket for IP: {} (last access: {})", 
                         entry.getKey(), entry.getValue().lastAccess());
            }
            return isInactive;
        });
        
        int removedCount = initialSize - buckets.size();
        if (removedCount > 0) {
            log.info("🧹 Cleanup completed: removed {} inactive IP buckets (remaining: {})", 
                     removedCount, buckets.size());
        }
    }
    
    /**
     * Extract client IP address from request.
     * Handles common proxy headers (X-Forwarded-For, X-Real-IP, CF-Connecting-IP).
     * 
     * @param request HTTP request
     * @return client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        // Check Cloudflare header first (most reliable if behind Cloudflare)
        String ip = request.getHeader("CF-Connecting-IP");
        
        // Check X-Forwarded-For (common proxy header)
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        
        // Check X-Real-IP (Nginx proxy)
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        // Check Proxy-Client-IP
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        // Check WL-Proxy-Client-IP (WebLogic)
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        
        // Fallback to remote address
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // X-Forwarded-For can contain multiple IPs (client, proxy1, proxy2, ...)
        // Take the first one (original client IP)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        // Validate IP format (basic check)
        if (ip != null && !isValidIp(ip)) {
            log.warn("⚠️ Invalid IP format detected: {}, using 'unknown'", ip);
            return "unknown";
        }
        
        return ip != null ? ip : "unknown";
    }
    
    /**
     * Basic IP address validation.
     * Checks for IPv4 and IPv6 format.
     * 
     * @param ip IP address to validate
     * @return true if valid format
     */
    private boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        // IPv4 pattern (simple check)
        if (ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            return true;
        }
        
        // IPv6 pattern (simple check)
        if (ip.contains(":") && ip.length() <= 45) {
            return true;
        }
        
        // Localhost variants
        if (ip.equals("localhost") || ip.equals("0:0:0:0:0:0:0:1")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get current rate limit statistics.
     * Useful for monitoring and debugging.
     * 
     * @return rate limit stats
     */
    public RateLimitStats getStats() {
        return new RateLimitStats(
            buckets.size(),
            requestsPerMinute,
            cleanupAfterMinutes
        );
    }
    
    /**
     * Clear all rate limit buckets.
     * Useful for testing or manual cleanup.
     */
    public void clearAllBuckets() {
        int count = buckets.size();
        buckets.clear();
        log.info("🗑️ Cleared all {} rate limit buckets", count);
    }
    
    /**
     * Record to store bucket with last access timestamp.
     * Helps track when bucket was last used for cleanup purposes.
     */
    private record BucketWithTimestamp(Bucket bucket, Instant lastAccess) {}
    
    /**
     * Rate limit statistics DTO.
     */
    public record RateLimitStats(
        int activeIpCount,
        int requestsPerMinute,
        int cleanupAfterMinutes
    ) {}
}