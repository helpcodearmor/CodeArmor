// SOME CASES IT WORKS BUT NOT ALWAYS (Back to basic)

// package com.Scanner.service;

// import com.Scanner.model.Vulnerability;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// /**
//  * Performs fast static pattern-based vulnerability detection.
//  * Optimized with deduplication and multi-line pattern support.
//  */
// @Slf4j
// @Service
// public class StaticCheckService {
    
//     // Pattern definitions for different vulnerability types
//     private static final List<VulnerabilityPattern> PATTERNS = List.of(
        
//         // ============================================
//         // XSS (Cross-Site Scripting) PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(innerHTML|outerHTML)\\s*=\\s*(?!([\"']|`)[^\"'`]*\\1)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.XSS,
//             Vulnerability.Severity.High,
//             "Direct DOM manipulation with innerHTML/outerHTML can lead to XSS attacks",
//             "Use textContent for plain text or sanitize input with DOMPurify library before assignment"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("document\\.write\\s*\\([^)]*\\)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.XSS,
//             Vulnerability.Severity.Medium,
//             "document.write() can introduce XSS vulnerabilities when used with user input",
//             "Use modern DOM methods like createElement(), appendChild(), or textContent instead"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("\\beval\\s*\\(", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.XSS,
//             Vulnerability.Severity.High,
//             "eval() executes arbitrary code and is a major security risk",
//             "Remove eval(); use JSON.parse() for JSON data or refactor to use safe alternatives"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("dangerouslySetInnerHTML\\s*=\\s*\\{\\{", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.XSS,
//             Vulnerability.Severity.High,
//             "dangerouslySetInnerHTML bypasses React's built-in XSS protection",
//             "Sanitize HTML with DOMPurify before using: dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(html)}}"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("<script[^>]*>|onerror\\s*=|onload\\s*=|javascript:\\s*|alert\\s*\\(|<iframe", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.XSS,
//             Vulnerability.Severity.High,
//             "Possible XSS payload detected (script tags, event handlers, or javascript: protocol)",
//             "Sanitize all user input, encode output, and use Content Security Policy (CSP) headers"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("\\.html\\s*\\((?!\\s*\\))", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.XSS,
//             Vulnerability.Severity.Medium,
//             "jQuery .html() method with dynamic content can lead to XSS",
//             "Use .text() for plain text or sanitize HTML content before using .html()"
//         ),
        
//         // ============================================
//         // SQL INJECTION PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "java",
//             Pattern.compile("(createStatement|Statement).*\\.(execute|executeQuery|executeUpdate)\\s*\\([^)]*\\+[^)]*\\)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SQLI,
//             Vulnerability.Severity.High,
//             "SQL query uses string concatenation which enables SQL injection attacks",
//             "Use PreparedStatement with parameterized queries: preparedStatement.setString(1, value)"
//         ),
        
//         new VulnerabilityPattern(
//             "java",
//             Pattern.compile("\"\\s*(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE)\\s+.*\\+", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SQLI,
//             Vulnerability.Severity.High,
//             "String concatenation in SQL statement allows SQL injection",
//             "Use PreparedStatement or ORM frameworks (Hibernate, JPA) with named parameters"
//         ),
        
//         new VulnerabilityPattern(
//             "python",
//             Pattern.compile("(execute|executemany|cursor\\.execute)\\s*\\([^)]*(%s|%d|\\%\\(|f[\"']).*[\"'][^)]*\\)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SQLI,
//             Vulnerability.Severity.High,
//             "Python SQL query uses string formatting which creates SQL injection vulnerability",
//             "Use parameterized queries: cursor.execute('SELECT * FROM users WHERE id = ?', (user_id,))"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(query|execute|sql)\\s*\\([^)]*`[^`]*\\$\\{[^}]+\\}[^`]*`", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SQLI,
//             Vulnerability.Severity.High,
//             "Template literals in SQL queries enable SQL injection in JavaScript/Node.js",
//             "Use parameterized queries with ? placeholders: db.query('SELECT * FROM users WHERE id = ?', [userId])"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(\\bOR\\b|\\bAND\\b)\\s+['\"]?\\d+['\"]?\\s*=\\s*['\"]?\\d+['\"]?|'\\s*OR\\s*'1'\\s*=\\s*'1|--\\s*$|#\\s*$|;\\s*DROP\\s+|UNION\\s+SELECT", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SQLI,
//             Vulnerability.Severity.High,
//             "SQL injection payload detected (OR 1=1, UNION SELECT, comment injection)",
//             "Never trust user input; use parameterized queries and input validation on the backend"
//         ),
        
//         new VulnerabilityPattern(
//             "php",
//             Pattern.compile("mysql_query\\s*\\([^)]*\\$[^)]*\\)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SQLI,
//             Vulnerability.Severity.High,
//             "PHP mysql_query with variable concatenation enables SQL injection",
//             "Use PDO or mysqli with prepared statements: $stmt->bind_param('s', $username)"
//         ),
        
//         // ============================================
//         // CSRF (Cross-Site Request Forgery) PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("<form[^>]+method\\s*=\\s*[\"']post[\"'][^>]*>(?:(?!</form>).)*?(?!csrf|_token|authenticity_token)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
//             Vulnerability.VulnerabilityType.CSRF,
//             Vulnerability.Severity.Medium,
//             "POST form lacks CSRF token protection",
//             "Add CSRF token: <input type=\"hidden\" name=\"_csrf\" value=\"${csrfToken}\"> and verify server-side"
//         ),
        
//         new VulnerabilityPattern(
//             "java",
//             Pattern.compile("@(PostMapping|PutMapping|DeleteMapping|PatchMapping)(?![^{]*@CsrfToken)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.CSRF,
//             Vulnerability.Severity.Medium,
//             "Spring endpoint modifies state without explicit CSRF protection",
//             "Enable Spring Security CSRF or document why it's disabled (e.g., stateless JWT API)"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(fetch|axios|\\$\\.ajax|\\$\\.post)\\s*\\([^)]*method\\s*:\\s*[\"'](POST|PUT|DELETE|PATCH)[\"'](?![^)]*csrf|[^)]*X-CSRF)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.CSRF,
//             Vulnerability.Severity.Medium,
//             "State-changing HTTP request without CSRF token in headers",
//             "Include CSRF token: headers: {'X-CSRF-Token': csrfToken} or use SameSite cookies"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("credentials\\s*:\\s*[\"']include[\"'](?![^}]*csrf)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.CSRF,
//             Vulnerability.Severity.Medium,
//             "Cross-origin request with credentials but no CSRF protection mentioned",
//             "Implement CSRF tokens for state-changing operations when using credentials: 'include'"
//         ),
        
//         // ============================================
//         // COMMAND INJECTION PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "python",
//             Pattern.compile("os\\.(system|popen)\\s*\\([^)]*\\+[^)]*\\)|subprocess\\.call\\s*\\([^)]*shell\\s*=\\s*True", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.High,
//             "Command injection vulnerability through os.system() or subprocess with shell=True",
//             "Use subprocess.run(['cmd', 'arg1', 'arg2'], shell=False) with argument list"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(exec|execSync|spawn|child_process)\\s*\\([^)]*\\+[^)]*\\)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.High,
//             "Command injection through exec/spawn with concatenated user input",
//             "Use execFile() or spawn() with array of arguments: spawn('cmd', [arg1, arg2])"
//         ),
        
//         new VulnerabilityPattern(
//             "php",
//             Pattern.compile("(exec|system|shell_exec|passthru|proc_open)\\s*\\([^)]*\\$", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.High,
//             "PHP command execution with user input enables command injection",
//             "Avoid shell commands with user input; use escapeshellarg() if unavoidable"
//         ),
        
//         // ============================================
//         // INSECURE DESERIALIZATION PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "java",
//             Pattern.compile("ObjectInputStream.*readObject\\s*\\(", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.High,
//             "Unsafe deserialization can lead to remote code execution",
//             "Validate serialized data source, use whitelist validation, or switch to JSON"
//         ),
        
//         new VulnerabilityPattern(
//             "python",
//             Pattern.compile("pickle\\.(loads|load)\\s*\\(", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.High,
//             "Pickle deserialization of untrusted data can execute arbitrary code",
//             "Never unpickle untrusted data; use JSON or implement safe serialization"
//         ),
        
//         // ============================================
//         // HARDCODED SECRETS PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(password|passwd|pwd|secret|token|api[_-]?key)\\s*[=:]\\s*[\"'][^\"']{8,}[\"']", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SECRET,
//             Vulnerability.Severity.High,
//             "Hardcoded sensitive credential detected in source code",
//             "Move to environment variables (.env), AWS Secrets Manager, or Azure Key Vault"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(jdbc|mongodb|mysql|postgresql|mssql):[^\\s\"']+:[^\\s\"']+@", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SECRET,
//             Vulnerability.Severity.High,
//             "Database connection string with embedded credentials found",
//             "Use environment variables: DB_URL, DB_USER, DB_PASS separately"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(aws_access_key_id|aws_secret_access_key|AKIA[0-9A-Z]{16})", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SECRET,
//             Vulnerability.Severity.High,
//             "AWS access key detected in code",
//             "Revoke this key immediately! Use IAM roles or AWS Secrets Manager instead"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(BEGIN (RSA|DSA|EC|OPENSSH) PRIVATE KEY)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.SECRET,
//             Vulnerability.Severity.High,
//             "Private cryptographic key found in source code",
//             "Remove immediately! Store keys in secure key management systems"
//         ),
        
//         // ============================================
//         // PATH TRAVERSAL PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("(File|FileInputStream|FileReader|open|fopen)\\s*\\([^)]*\\+[^)]*\\)", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.High,
//             "Path traversal vulnerability through file path concatenation",
//             "Validate file paths, use Path.normalize(), and restrict to allowed directories"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("\\.\\./|\\.\\.\\/|%2e%2e%2f|%252e%252e%252f", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.Medium,
//             "Path traversal sequence detected (../ or encoded variants)",
//             "Sanitize file paths and validate against whitelist of allowed files"
//         ),
        
//         // ============================================
//         // WEAK CRYPTOGRAPHY PATTERNS
//         // ============================================
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("\\b(MD5|SHA1|DES|RC4)\\b", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.Medium,
//             "Weak or broken cryptographic algorithm detected",
//             "Use SHA-256, SHA-3, or bcrypt for hashing; AES-256 for encryption"
//         ),
        
//         new VulnerabilityPattern(
//             "all",
//             Pattern.compile("Math\\.random\\(\\).*password|Math\\.random\\(\\).*token|Math\\.random\\(\\).*secret", Pattern.CASE_INSENSITIVE),
//             Vulnerability.VulnerabilityType.OTHER,
//             Vulnerability.Severity.High,
//             "Using Math.random() for security-sensitive random values",
//             "Use crypto.randomBytes() (Node.js) or SecureRandom (Java) for cryptographic randomness"
//         )
//     );
    
//     /**
//      * Perform static pattern-based checks on code.
//      * Optimized with deduplication and better performance.
//      * 
//      * @param code the source code to scan
//      * @param language the programming language
//      * @return list of detected vulnerabilities
//      */
//     public List<Vulnerability> performStaticChecks(String code, String language) {
//         log.debug("Performing static checks for language: {}", language);
        
//         List<Vulnerability> vulnerabilities = new ArrayList<>();
//         Set<String> seenVulnerabilities = new HashSet<>(); // Deduplication
        
//         String[] lines = code.split("\\r?\\n");
//         String normalizedLanguage = language.toLowerCase();
        
//         int vulnCounter = 1;
        
//         // Optimize: Check full code for multi-line patterns first
//         for (VulnerabilityPattern pattern : PATTERNS) {
//             if (!pattern.language.equals("all") && !pattern.language.equals(normalizedLanguage)) {
//                 continue;
//             }
            
//             // For patterns with DOTALL flag (multi-line), scan entire code
//             if ((pattern.pattern.flags() & Pattern.DOTALL) != 0) {
//                 Matcher matcher = pattern.pattern.matcher(code);
//                 if (matcher.find()) {
//                     String dedupKey = "full-" + pattern.type + "-" + pattern.reason;
//                     if (seenVulnerabilities.add(dedupKey)) {
//                         vulnerabilities.add(createVulnerability(
//                             "static-" + vulnCounter++,
//                             1, // Multi-line patterns show at line 1
//                             pattern,
//                             code.substring(matcher.start(), Math.min(matcher.end(), matcher.start() + 100))
//                         ));
//                     }
//                 }
//                 continue; // Skip line-by-line for this pattern
//             }
            
//             // Line-by-line scanning for single-line patterns
//             for (int i = 0; i < lines.length; i++) {
//                 String line = lines[i];
//                 Matcher matcher = pattern.pattern.matcher(line);
                
//                 if (matcher.find()) {
//                     // Deduplication: same type + line = one vulnerability
//                     String dedupKey = (i + 1) + "-" + pattern.type;
                    
//                     if (seenVulnerabilities.add(dedupKey)) {
//                         String snippet = line.trim();
//                         if (snippet.length() > 100) {
//                             snippet = snippet.substring(0, 97) + "...";
//                         }
                        
//                         vulnerabilities.add(createVulnerability(
//                             "static-" + vulnCounter++,
//                             i + 1,
//                             pattern,
//                             snippet
//                         ));
                        
//                         log.debug("Found {} at line {}", pattern.type, i + 1);
//                     }
//                 }
//             }
//         }
        
//         log.info("Static checks completed. Found {} unique vulnerabilities", vulnerabilities.size());
//         return vulnerabilities;
//     }
    
//     /**
//      * Helper method to create vulnerability objects
//      */
//     private Vulnerability createVulnerability(String id, int line, VulnerabilityPattern pattern, String snippet) {
//         return Vulnerability.builder()
//                 .id(id)
//                 .line(line)
//                 .type(pattern.type)
//                 .severity(pattern.severity)
//                 .snippet(snippet.length() > 100 ? snippet.substring(0, 97) + "..." : snippet)
//                 .reason(pattern.reason)
//                 .suggestion(pattern.suggestion)
//                 .build();
//     }
    
//     /**
//      * Internal class to hold pattern definitions
//      */
//     private static class VulnerabilityPattern {
//         final String language;
//         final Pattern pattern;
//         final Vulnerability.VulnerabilityType type;
//         final Vulnerability.Severity severity;
//         final String reason;
//         final String suggestion;
        
//         VulnerabilityPattern(String language, Pattern pattern, 
//                            Vulnerability.VulnerabilityType type,
//                            Vulnerability.Severity severity,
//                            String reason, String suggestion) {
//             this.language = language;
//             this.pattern = pattern;
//             this.type = type;
//             this.severity = severity;
//             this.reason = reason;
//             this.suggestion = suggestion;
//         }
//     }
// }







package com.Scanner.service;

import com.Scanner.model.Vulnerability;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced StaticCheckService with comprehensive vulnerability detection
 * Covers: SQL Injection, XSS, CSRF, Secrets, Command Injection, Path Traversal,
 * HTML Context vulnerabilities, and more
 */
@Slf4j
@Service
public class StaticCheckService {

    private static final List<VulnerabilityPattern> PATTERNS = List.of(

        // =======================================
        // XSS (Cross-Site Scripting) - JavaScript
        // =======================================
        new VulnerabilityPattern(
            "javascript",
            Pattern.compile(
                "res\\.send\\s*\\(.*(\\+|\\$\\{).*\\)", 
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.XSS,
            Vulnerability.Severity.High,
            "User input may be injected into HTML response (Reflected XSS risk).",
            "Sanitize the output (DOMPurify/validator) or escape HTML before returning."
        ),

        new VulnerabilityPattern(
            "javascript",
            Pattern.compile(
                "(innerHTML|outerHTML)\\s*=\\s*",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.XSS,
            Vulnerability.Severity.High,
            "Direct DOM HTML assignment detected, may allow injection of malicious scripts.",
            "Use textContent / innerText or sanitize HTML content with DOMPurify."
        ),

        new VulnerabilityPattern(
            "javascript",
            Pattern.compile(
                "document\\.write\\s*\\(",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.XSS,
            Vulnerability.Severity.High,
            "document.write() can enable XSS if user input is included.",
            "Avoid document.write(); use DOM manipulation methods instead."
        ),

        new VulnerabilityPattern(
            "javascript",
            Pattern.compile(
                "eval\\s*\\(",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.XSS,
            Vulnerability.Severity.High,
            "eval() executes arbitrary code and is extremely dangerous with user input.",
            "Never use eval(). Use JSON.parse() or safer alternatives."
        ),

        // =======================================
        // XSS - HTML Context Detection
        // =======================================
        new VulnerabilityPattern(
            "html",
            Pattern.compile(
                "<%=\\s*[^%]*\\s*%>",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.XSS,
            Vulnerability.Severity.High,
            "Direct output of variables in HTML without encoding can lead to XSS.",
            "Use proper encoding: <%- variable %> for HTML escaping or sanitize with a library."
        ),

        new VulnerabilityPattern(
            "html",
            Pattern.compile(
                "<script[^>]*>[^<]*\\$\\{[^}]+\\}[^<]*</script>",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.XSS,
            Vulnerability.Severity.Critical,
            "JavaScript injection detected: variable interpolation inside <script> tags.",
            "Never inject user data directly into <script> tags. Use data attributes and parse safely."
        ),

        new VulnerabilityPattern(
            "html",
            Pattern.compile(
                "on(click|load|error|mouseover|focus|blur)\\s*=\\s*[\"'][^\"']*\\$\\{",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.XSS,
            Vulnerability.Severity.High,
            "Event handler with dynamic content can execute malicious scripts.",
            "Avoid inline event handlers with user input. Use addEventListener() instead."
        ),

        // =======================================
        // CSRF Detection - Enhanced
        // =======================================
        new VulnerabilityPattern(
            "javascript",
            Pattern.compile(
                "(app|router)\\.(post|put|patch|delete)\\s*\\([^)]*\\)\\s*\\{(?![^}]*csrf)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.CSRF,
            Vulnerability.Severity.High,
            "State-changing API endpoint without CSRF validation.",
            "Implement CSRF middleware, same-site cookies, and CSRF token verification."
        ),

        new VulnerabilityPattern(
            "html",
            Pattern.compile(
                "<form[^>]*method\\s*=\\s*[\"']?(post|put|patch|delete)[\"']?[^>]*>(?:(?!</form>).)*</form>",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.CSRF,
            Vulnerability.Severity.Medium,
            "HTML form with state-changing method detected. Verify CSRF protection exists.",
            "Ensure forms include CSRF tokens: <input type='hidden' name='_csrf' value='...'>"
        ),

        // Special pattern to detect forms WITHOUT CSRF token
        new VulnerabilityPattern(
            "html",
            Pattern.compile(
                "<form[^>]*method\\s*=\\s*[\"']?(post|put|patch|delete)[\"']?[^>]*>(?:(?!csrf|_csrf|csrfToken|authenticity_token)(?!</form>).){0,500}</form>",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.CSRF,
            Vulnerability.Severity.High,
            "Form with state-changing method is missing CSRF token protection.",
            "Add CSRF token: <input type='hidden' name='_csrf' value='<%= csrfToken %>'>"
        ),

        // =======================================
        // SQL Injection Detection - Enhanced
        // =======================================
        new VulnerabilityPattern(
            "all",
            Pattern.compile(
                "(SELECT|UPDATE|INSERT|DELETE)[^;]+(\\+|\\$\\{|\\%s|\\?.*\\+)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.SQLI,
            Vulnerability.Severity.High,
            "SQL query constructed with string concatenation, injection possible.",
            "Use parameterized/prepared statements: PreparedStatement, query placeholders, or ORM."
        ),

        new VulnerabilityPattern(
            "java",
            Pattern.compile(
                "createStatement\\s*\\(\\s*\\).*executeQuery\\s*\\(",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.SQLI,
            Vulnerability.Severity.High,
            "Using Statement instead of PreparedStatement can lead to SQL injection.",
            "Use PreparedStatement with parameterized queries."
        ),

        new VulnerabilityPattern(
            "python",
            Pattern.compile(
                "execute\\s*\\([^)]*\\%[^)]*\\)",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.SQLI,
            Vulnerability.Severity.High,
            "SQL query using string formatting (%) can allow injection.",
            "Use parameterized queries: cursor.execute(sql, (param1, param2))"
        ),

        new VulnerabilityPattern(
            "php",
            Pattern.compile(
                "mysql_query\\s*\\([^)]*\\$_(GET|POST|REQUEST)",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.SQLI,
            Vulnerability.Severity.Critical,
            "Direct use of user input in MySQL query enables SQL injection.",
            "Use PDO prepared statements with parameter binding."
        ),

        // =======================================
        // Hardcoded Secrets / Credentials
        // =======================================
        new VulnerabilityPattern(
            "all",
            Pattern.compile(
                "(password|secret|token|api[_-]?key|private[_-]?key)\\s*[:=]\\s*[\"'][a-zA-Z0-9\\-_!@#$%^&*()+=]{8,}[\"']",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.SECRET,
            Vulnerability.Severity.High,
            "Hardcoded credentials found inside the code.",
            "Move secrets into environment variables (.env) or a secrets manager (AWS Secrets, HashiCorp Vault)."
        ),

        new VulnerabilityPattern(
            "all",
            Pattern.compile(
                "(Bearer\\s+[a-zA-Z0-9\\-_\\.]{20,}|ghp_[a-zA-Z0-9]{36}|sk_[a-z]+_[a-zA-Z0-9]{24,})",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.SECRET,
            Vulnerability.Severity.Critical,
            "Hardcoded API token or access key detected in code.",
            "Remove hardcoded tokens immediately. Use environment variables and rotate exposed keys."
        ),

        // =======================================
        // Command Injection Detection
        // =======================================
        new VulnerabilityPattern(
            "javascript",
            Pattern.compile(
                "(exec|spawn|execSync|execFile)\\s*\\([^)]*(\\+|\\$\\{|\\`\\$\\{)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.DI,
            Vulnerability.Severity.High,
            "Command execution using untrusted input allows system compromise.",
            "Validate input strictly, use parameterized execution, or avoid shell commands entirely."
        ),

        new VulnerabilityPattern(
            "python",
            Pattern.compile(
                "(os\\.system|subprocess\\.call|subprocess\\.Popen|eval)\\s*\\([^)]*\\+",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.DI,
            Vulnerability.Severity.High,
            "Command injection risk: concatenating user input into system commands.",
            "Use subprocess with argument lists instead of shell=True, validate all inputs."
        ),

        new VulnerabilityPattern(
            "php",
            Pattern.compile(
                "(exec|system|passthru|shell_exec|``|popen|proc_open)\\s*\\([^)]*\\$_(GET|POST|REQUEST)",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.DI,
            Vulnerability.Severity.Critical,
            "Direct execution of user input as system command.",
            "Avoid executing shell commands with user input. Use escapeshellarg() if unavoidable."
        ),

        // =======================================
        // Path Traversal Detection
        // =======================================
        new VulnerabilityPattern(
            "javascript",
            Pattern.compile(
                "fs\\.(readFile|readFileSync|createReadStream|writeFile)\\s*\\([^)]*(\\+|\\$\\{|req\\.)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.High,
            "File path uses user input, path traversal risk exists (e.g., ../../etc/passwd).",
            "Validate allowed path names, use path.resolve() and restrict to safe directories."
        ),

        new VulnerabilityPattern(
            "python",
            Pattern.compile(
                "open\\s*\\([^)]*\\+[^)]*\\)",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.High,
            "File path construction with concatenation can allow path traversal.",
            "Use os.path.join() and validate paths against a whitelist of allowed directories."
        ),

        new VulnerabilityPattern(
            "java",
            Pattern.compile(
                "new\\s+File\\s*\\([^)]*\\+",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.High,
            "File path concatenation can enable path traversal attacks.",
            "Use Path.of() with validation, check canonical paths against allowed base directories."
        ),

        // =======================================
        // Insecure Deserialization
        // =======================================
        new VulnerabilityPattern(
            "java",
            Pattern.compile(
                "ObjectInputStream|readObject\\s*\\(",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.High,
            "Deserialization of untrusted data can lead to remote code execution.",
            "Avoid deserializing untrusted data. Use JSON instead of Java serialization."
        ),

        new VulnerabilityPattern(
            "python",
            Pattern.compile(
                "pickle\\.loads?\\s*\\(",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.High,
            "Pickle deserialization from untrusted sources enables code execution.",
            "Never unpickle untrusted data. Use JSON or message pack for data serialization."
        ),

        // =======================================
        // Insecure Direct Object Reference (IDOR)
        // =======================================
        new VulnerabilityPattern(
            "all",
            Pattern.compile(
                "findById\\s*\\([^)]*req\\.(params|query|body)",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.Medium,
            "Direct use of user-supplied ID without authorization check (IDOR risk).",
            "Verify user has permission to access the requested resource before returning data."
        ),

        // =======================================
        // Weak Cryptography
        // =======================================
        new VulnerabilityPattern(
            "all",
            Pattern.compile(
                "(MD5|SHA1)\\s*\\(",
                Pattern.CASE_INSENSITIVE
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.Medium,
            "Use of weak cryptographic hash function (MD5/SHA1).",
            "Use stronger algorithms: SHA-256, SHA-3, bcrypt, or argon2 for passwords."
        ),

        // =======================================
        // Open Redirect
        // =======================================
        new VulnerabilityPattern(
            "all",
            Pattern.compile(
                "(res\\.redirect|window\\.location|header\\s*\\([^)]*Location)\\s*\\([^)]*req\\.(query|params|body)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            ),
            Vulnerability.VulnerabilityType.OTHER,
            Vulnerability.Severity.Medium,
            "Unvalidated redirect using user input can enable phishing attacks.",
            "Validate redirect URLs against a whitelist of allowed domains."
        )
    );

    public List<Vulnerability> performStaticChecks(String code, String language) {
        log.debug("Performing static checks. Language: {}", language);
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        Set<String> detected = new HashSet<>();

        String normalizedLanguage = language == null ? "all" : language.toLowerCase();
        
        // Detect if content is HTML
        if (code.trim().startsWith("<!") || code.trim().startsWith("<html") || 
            code.toLowerCase().contains("<form") || code.toLowerCase().contains("<script")) {
            normalizedLanguage = "html";
            log.info("Detected HTML content, switching to HTML analysis mode");
        }
        
        String[] lines = code.split("\\r?\\n");
        int counter = 1;

        for (VulnerabilityPattern vp : PATTERNS) {
            if (!vp.language.equals("all") && 
                !vp.language.equals(normalizedLanguage) &&
                !(vp.language.equals("javascript") && normalizedLanguage.equals("html"))) {
                continue;
            }

            Matcher mm = vp.pattern.matcher(code);

            while (mm.find()) {
                int line = getLineNumber(lines, mm.start());
                String snippet = code.substring(mm.start(), Math.min(code.length(), mm.start() + 120))
                    .trim()
                    .replace("\n", " ")
                    .replace("\r", "");

                String key = vp.type + "-" + line + "-" + mm.start();
                if (detected.add(key)) {
                    vulnerabilities.add(
                        Vulnerability.builder()
                            .id("static-" + counter++)
                            .line(line)
                            .type(vp.type)
                            .severity(vp.severity)
                            .snippet(snippet)
                            .reason(vp.reason)
                            .suggestion(vp.suggestion)
                            .build()
                    );
                }
            }
        }

        log.info("Static scan finished. Total vulnerabilities found: {}", vulnerabilities.size());
        return vulnerabilities;
    }

    private int getLineNumber(String[] lines, int pos) {
        int count = 1, index = 0;
        for (String l : lines) {
            index += l.length() + 1;
            if (index > pos) return count;
            count++;
        }
        return count;
    }

    private record VulnerabilityPattern(
        String language, 
        Pattern pattern,
        Vulnerability.VulnerabilityType type,
        Vulnerability.Severity severity,
        String reason, 
        String suggestion
    ) {}
}