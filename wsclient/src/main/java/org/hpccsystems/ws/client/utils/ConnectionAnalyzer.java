package org.hpccsystems.ws.client.utils;

/*##############################################################################

HPCC SYSTEMS software Copyright (C) 2025 HPCC Systems®.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
############################################################################## */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * Analyzes and diagnoses wsclient connection issues to HPCC Systems clusters.
 * 
 * This class provides comprehensive diagnostics for connection problems including:
 * - Invalid connection configuration values
 * - Invalid/missing credentials
 * - Network connectivity issues (DNS resolution, host reachability)
 * - Firewall issues (port accessibility)
 * - Network latency/congestion
 * - SSL/TLS certificate issues
 * - Target service availability
 * - Authentication/authorization issues
 */
public class ConnectionAnalyzer
{
    private static final Logger log = LogManager.getLogger(ConnectionAnalyzer.class);

    /**
     * Represents the result of a connection analysis.
     */
    public static class AnalysisResult
    {
        private final boolean successful;
        private final List<String> warnings;
        private final List<String> errors;
        private final List<String> recommendations;
        private final long latencyMs;

        public AnalysisResult(boolean successful, List<String> warnings, List<String> errors,
                List<String> recommendations, long latencyMs)
        {
            this.successful = successful;
            this.warnings = new ArrayList<>(warnings);
            this.errors = new ArrayList<>(errors);
            this.recommendations = new ArrayList<>(recommendations);
            this.latencyMs = latencyMs;
        }

        /**
         * Indicates if the connection analysis was successful.
         * 
         * @return true if connection is viable, false otherwise
         */
        public boolean isSuccessful()
        {
            return successful;
        }

        /**
         * Gets warning messages encountered during analysis.
         * 
         * @return list of warning messages
         */
        public List<String> getWarnings()
        {
            return new ArrayList<>(warnings);
        }

        /**
         * Gets error messages encountered during analysis.
         * 
         * @return list of error messages
         */
        public List<String> getErrors()
        {
            return new ArrayList<>(errors);
        }

        /**
         * Gets recommendations for resolving connection issues.
         * 
         * @return list of recommendations
         */
        public List<String> getRecommendations()
        {
            return new ArrayList<>(recommendations);
        }

        /**
         * Gets the measured network latency in milliseconds.
         * 
         * @return latency in milliseconds, or -1 if not measured
         */
        public long getLatencyMs()
        {
            return latencyMs;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Connection Analysis Result:\n");
            sb.append("Status: ").append(successful ? "SUCCESS" : "FAILURE").append("\n");

            if (latencyMs >= 0)
            {
                sb.append("Latency: ").append(latencyMs).append(" ms\n");
            }

            if (!errors.isEmpty())
            {
                sb.append("\nErrors:\n");
                for (String error : errors)
                {
                    sb.append("  - ").append(error).append("\n");
                }
            }

            if (!warnings.isEmpty())
            {
                sb.append("\nWarnings:\n");
                for (String warning : warnings)
                {
                    sb.append("  - ").append(warning).append("\n");
                }
            }

            if (!recommendations.isEmpty())
            {
                sb.append("\nRecommendations:\n");
                for (String recommendation : recommendations)
                {
                    sb.append("  - ").append(recommendation).append("\n");
                }
            }

            return sb.toString();
        }
    }

    /**
     * Performs a comprehensive analysis of the connection.
     * 
     * @param connection the connection to analyze
     * @return analysis result containing diagnostics and recommendations
     */
    @WithSpan
    public static AnalysisResult analyzeConnection(Connection connection)
    {
        log.info("Starting connectivity analysis for: {}", connection.getUrl());
        
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        long latencyMs = -1;
        boolean overallSuccess = true;

        // Validate connection configuration
        log.debug("Step 1: Validating connection configuration");
        if (!validateConnectionConfiguration(connection, errors, warnings, recommendations))
        {
            log.error("Connection configuration validation failed for: {}", connection.getUrl());
            overallSuccess = false;
        }
        else
        {
            log.info("Connection configuration validation passed for: {}", connection.getUrl());
        }

        // Test DNS resolution
        String hostname = connection.getHost();
        log.debug("Step 2: Testing DNS resolution for hostname: {}", hostname);
        InetAddress address = null;
        if (!testDNSResolution(hostname, errors, warnings, recommendations))
        {
            log.error("DNS resolution failed for hostname: {} - cannot proceed with connectivity tests", hostname);
            overallSuccess = false;
        }
        else
        {
            log.info("DNS resolution successful for hostname: {}", hostname);
            try
            {
                address = InetAddress.getByName(hostname);
                log.debug("Resolved {} to IP address: {}", hostname, address.getHostAddress());
            }
            catch (UnknownHostException e)
            {
                // Already logged in testDNSResolution
                log.error("Failed to get InetAddress for hostname: {}", hostname, e);
            }
        }

        // Test network connectivity and latency
        if (address != null)
        {
            log.debug("Step 3: Testing network connectivity to {}:{}", address.getHostAddress(), connection.getPortInt());
            latencyMs = testNetworkConnectivity(address, connection.getPortInt(), errors, warnings, recommendations);
            if (latencyMs < 0)
            {
                log.error("Network connectivity test failed to {}:{} - connection cannot be established", 
                    address.getHostAddress(), connection.getPortInt());
                overallSuccess = false;
            }
            else if (latencyMs > 1000)
            {
                log.warn("High network latency detected: {} ms to {}:{}", latencyMs, address.getHostAddress(), connection.getPortInt());
                warnings.add("High network latency detected: " + latencyMs + " ms");
                recommendations.add("Consider using a closer HPCC cluster or investigating network congestion");
            }
            else
            {
                log.info("Network connectivity test successful - latency: {} ms", latencyMs);
            }
        }
        else
        {
            log.error("Cannot test network connectivity - IP address is null (DNS resolution failed)");
            overallSuccess = false;
        }

        // Test SSL/TLS if using HTTPS
        if (connection.getIsHttps() && address != null)
        {
            log.debug("Step 4: Testing SSL/TLS certificate for HTTPS connection to {}:{}", 
                connection.getHost(), connection.getPortInt());
            if (!testSSLCertificate(connection, errors, warnings, recommendations))
            {
                log.error("SSL/TLS certificate validation failed for {}:{}", connection.getHost(), connection.getPortInt());
                overallSuccess = false;
            }
            else
            {
                log.info("SSL/TLS certificate validation passed for {}:{}", connection.getHost(), connection.getPortInt());
            }
        }

        // Test HTTP endpoint availability
        if (address != null)
        {
            log.debug("Step 5: Testing HTTP endpoint availability for {}", connection.getBaseUrl());
            if (!testHTTPEndpoint(connection, errors, warnings, recommendations))
            {
                log.error("HTTP endpoint test failed for {} - service may not be running or accessible", connection.getBaseUrl());
                overallSuccess = false;
            }
            else
            {
                log.info("HTTP endpoint test passed for {}", connection.getBaseUrl());
            }
        }

        // Test authentication if credentials are provided
        if (connection.hasCredentials())
        {
            log.debug("Step 6: Testing authentication with provided credentials for {}", connection.getBaseUrl());
            if (!testAuthentication(connection, errors, warnings, recommendations))
            {
                log.error("Authentication test failed for {} - credentials may be invalid", connection.getBaseUrl());
                overallSuccess = false;
            }
            else
            {
                log.info("Authentication test passed for {}", connection.getBaseUrl());
            }
        }
        else
        {
            log.debug("Step 6: Skipping authentication test - no credentials provided");
        }

        // Test HPCC-specific endpoints used by BaseHPCCWsClient
        if (address != null)
        {
            log.debug("Step 7: Testing HPCC-specific endpoints for {}", connection.getBaseUrl());
            if (!testHPCCEndpoints(connection, errors, warnings, recommendations))
            {
                log.warn("Some HPCC-specific endpoints are not accessible for {} - BaseHPCCWsClient may fail during initialization", 
                    connection.getBaseUrl());
                // Don't fail overall on HPCC endpoint tests as basic connectivity might still work
                warnings.add("Some HPCC-specific endpoints are not accessible");
            }
            else
            {
                log.info("All HPCC-specific endpoint tests passed for {}", connection.getBaseUrl());
            }
        }
        else
        {
            log.warn("Step 7: Skipping HPCC endpoint tests - network connectivity not established");
        }

        log.info("Connectivity analysis completed for {} - Overall status: {}", 
            connection.getUrl(), overallSuccess ? "SUCCESS" : "FAILURE");
        
        if (!overallSuccess)
        {
            log.error("Connectivity analysis identified {} errors and {} warnings for {}", 
                errors.size(), warnings.size(), connection.getUrl());
            if (!errors.isEmpty())
            {
                log.error("Errors encountered: {}", String.join("; ", errors));
            }
            if (!warnings.isEmpty())
            {
                log.warn("Warnings encountered: {}", String.join("; ", warnings));
            }
        }

        return new AnalysisResult(overallSuccess, warnings, errors, recommendations, latencyMs);
    }

    /**
     * Validates the connection configuration for common issues.
     * 
     * @param connection the connection to validate
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return true if configuration is valid, false otherwise
     */
    private static boolean validateConnectionConfiguration(Connection connection, List<String> errors,
            List<String> warnings, List<String> recommendations)
    {
        boolean valid = true;

        // Validate URL
        try
        {
            new URL(connection.getUrl());
            log.debug("Connection URL is well-formed: {}", connection.getUrl());
        }
        catch (MalformedURLException e)
        {
            String errorMsg = "Invalid connection URL: " + e.getMessage();
            errors.add(errorMsg);
            recommendations.add("Check the connection URL format: " + connection.getUrl());
            log.error("Connection URL validation failed: {} - URL: {}", e.getMessage(), connection.getUrl());
            valid = false;
        }

        // Validate host
        String host = connection.getHost();
        if (host == null || host.trim().isEmpty())
        {
            String errorMsg = "Host is null or empty";
            errors.add(errorMsg);
            recommendations.add("Specify a valid host name or IP address");
            log.error("Connection host validation failed: host is null or empty");
            valid = false;
        }
        else if (host.contains("_"))
        {
            String errorMsg = "Host name contains invalid underscores: " + host;
            errors.add(errorMsg);
            recommendations.add("Use a valid hostname or IP address without underscores");
            log.error("Connection host validation failed: hostname '{}' contains invalid underscores", host);
            valid = false;
        }
        else
        {
            log.debug("Connection host validation passed: {}", host);
        }

        // Validate port
        int port = connection.getPortInt();
        if (port < 0 || port > 65535)
        {
            String errorMsg = "Invalid port number: " + port;
            errors.add(errorMsg);
            recommendations.add("Use a valid port number between 1 and 65535");
            log.error("Connection port validation failed: port {} is out of valid range (1-65535)", port);
            valid = false;
        }
        else if (connection.getIsHttps() && port == 8010)
        {
            String warningMsg = "Using HTTP default port (8010) with HTTPS protocol";
            warnings.add(warningMsg);
            recommendations.add("Consider using HTTPS default port 18010 for HTTPS connections");
            log.warn("Port/protocol mismatch detected: using HTTP port 8010 with HTTPS protocol for host {}", host);
        }
        else if (!connection.getIsHttps() && port == 18010)
        {
            String warningMsg = "Using HTTPS default port (18010) with HTTP protocol";
            warnings.add(warningMsg);
            recommendations.add("Consider using HTTP default port 8010 for HTTP connections");
            log.warn("Port/protocol mismatch detected: using HTTPS port 18010 with HTTP protocol for host {}", host);
        }
        else
        {
            log.debug("Connection port validation passed: {}", port);
        }

        // Validate protocol
        String protocol = connection.getProtocol();
        if (protocol == null || (!protocol.equalsIgnoreCase("http") && !protocol.equalsIgnoreCase("https")))
        {
            String errorMsg = "Invalid protocol: " + protocol;
            errors.add(errorMsg);
            recommendations.add("Use either 'http' or 'https' protocol");
            log.error("Connection protocol validation failed: protocol '{}' is not http or https", protocol);
            valid = false;
        }
        else
        {
            log.debug("Connection protocol validation passed: {}", protocol);
        }

        // Check timeout values
        if (connection.getConnectTimeoutMilli() <= 0)
        {
            String warningMsg = "Invalid connect timeout: " + connection.getConnectTimeoutMilli();
            warnings.add(warningMsg);
            recommendations.add("Set a positive connect timeout value");
            log.warn("Connection timeout configuration issue: connect timeout is {} ms (should be positive)", 
                connection.getConnectTimeoutMilli());
        }

        if (connection.getReadTimeoutMilli() <= 0)
        {
            String warningMsg = "Invalid read timeout: " + connection.getReadTimeoutMilli();
            warnings.add(warningMsg);
            recommendations.add("Set a positive read timeout value");
            log.warn("Connection timeout configuration issue: read timeout is {} ms (should be positive)", 
                connection.getReadTimeoutMilli());
        }

        log.debug("Connection configuration validation completed - valid: {}", valid);
        return valid;
    }

    /**
     * Tests DNS resolution for the connection hostname.
     * 
     * @param hostname the hostname to resolve
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return true if DNS resolution succeeds, false otherwise
     */
    @WithSpan
    private static boolean testDNSResolution(String hostname, List<String> errors, List<String> warnings,
            List<String> recommendations)
    {
        try
        {
            InetAddress address = InetAddress.getByName(hostname);
            log.info("DNS resolution successful for {}: {}", hostname, address.getHostAddress());

            // Check if hostname resolves to localhost
            if (address.isLoopbackAddress())
            {
                warnings.add("Hostname resolves to localhost: " + hostname);
                recommendations.add("Ensure the HPCC cluster is accessible on localhost");
            }

            return true;
        }
        catch (UnknownHostException e)
        {
            errors.add("DNS resolution failed for hostname: " + hostname);
            recommendations.add("Check hostname spelling and DNS server configuration");
            recommendations.add("Try using IP address instead of hostname");
            log.error("DNS resolution failed for {}: {}", hostname, e.getMessage());
            return false;
        }
    }

    /**
     * Tests network connectivity to the target host and port.
     * 
     * @param address the resolved IP address
     * @param port the target port
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return latency in milliseconds, or -1 if connection fails
     */
    @WithSpan
    private static long testNetworkConnectivity(InetAddress address, int port, List<String> errors,
            List<String> warnings, List<String> recommendations)
    {
        long startTime = System.currentTimeMillis();

        try
        {
            log.debug("Attempting to connect to {}:{} with 5 second timeout", address.getHostAddress(), port);
            
            // Test port connectivity with timeout
            Socket socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(address, port), 5000);
            socket.close();

            long latency = System.currentTimeMillis() - startTime;
            log.info("Network connectivity test successful to {}:{} - latency: {} ms", address.getHostAddress(), port,
                    latency);
            
            if (latency > 500)
            {
                log.debug("Network latency is elevated: {} ms (consider investigating if > 1000ms)", latency);
            }
            
            return latency;
        }
        catch (SocketTimeoutException e)
        {
            String errorMsg = "Connection timeout to " + address.getHostAddress() + ":" + port;
            errors.add(errorMsg);
            recommendations.add("Check if the HPCC service is running");
            recommendations.add("Verify firewall rules allow connections to port " + port);
            recommendations.add("Check for network congestion or routing issues");
            log.error("Network connectivity test failed: connection timeout after 5 seconds to {}:{} - " +
                "Service may not be running, port may be blocked, or network routing issues exist", 
                address.getHostAddress(), port, e);
            return -1;
        }
        catch (IOException e)
        {
            String errorMsg = "Network connection failed to " + address.getHostAddress() + ":" + port + ": " + e.getMessage();
            errors.add(errorMsg);
            recommendations.add("Verify the HPCC service is running on port " + port);
            recommendations.add("Check firewall rules and network security groups");
            recommendations.add("Ensure the port is not blocked by container network policies");
            
            // Provide more specific guidance based on the exception message
            String exceptionMsg = e.getMessage().toLowerCase();
            if (exceptionMsg.contains("refused"))
            {
                log.error("Network connectivity test failed: connection refused to {}:{} - " +
                    "Service is not listening on this port or firewall is blocking the connection", 
                    address.getHostAddress(), port, e);
            }
            else if (exceptionMsg.contains("unreachable"))
            {
                log.error("Network connectivity test failed: host unreachable {}:{} - " +
                    "Network routing issue, host is down, or firewall is blocking ICMP/TCP", 
                    address.getHostAddress(), port, e);
            }
            else if (exceptionMsg.contains("no route"))
            {
                log.error("Network connectivity test failed: no route to host {}:{} - " +
                    "Network routing configuration issue or host is not reachable", 
                    address.getHostAddress(), port, e);
            }
            else
            {
                log.error("Network connectivity test failed to {}:{}: {} - Check network configuration and service availability", 
                    address.getHostAddress(), port, e.getMessage(), e);
            }
            
            return -1;
        }
    }

    /**
     * Tests SSL/TLS certificate validity for HTTPS connections.
     * 
     * @param connection the connection to test
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return true if certificate is valid or connection succeeds, false otherwise
     */
    @WithSpan
    private static boolean testSSLCertificate(Connection connection, List<String> errors, List<String> warnings,
            List<String> recommendations)
    {
        String hostname = connection.getHost();
        int port = connection.getPortInt();

        log.debug("Starting SSL/TLS certificate validation for {}:{}", hostname, port);

        try
        {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port);
            socket.startHandshake();

            log.debug("SSL handshake successful for {}:{}", hostname, port);

            Certificate[] certs = socket.getSession().getPeerCertificates();
            log.debug("Retrieved {} certificate(s) from server", certs.length);
            
            if (certs.length > 0 && certs[0] instanceof X509Certificate)
            {
                X509Certificate cert = (X509Certificate) certs[0];

                // Check certificate validity
                try
                {
                    cert.checkValidity();
                    log.info("SSL certificate is valid for {} - Subject: {}", hostname, 
                        cert.getSubjectX500Principal().getName());
                }
                catch (CertificateExpiredException e)
                {
                    String errorMsg = "SSL certificate has expired";
                    errors.add(errorMsg);
                    recommendations.add("Renew the SSL certificate on the HPCC cluster");
                    log.error("SSL certificate validation failed for {}: certificate has expired on {} - Subject: {}", 
                        hostname, cert.getNotAfter(), cert.getSubjectX500Principal().getName());
                    socket.close();
                    return false;
                }
                catch (CertificateNotYetValidException e)
                {
                    String errorMsg = "SSL certificate is not yet valid";
                    errors.add(errorMsg);
                    recommendations.add("Check system clock or wait for certificate to become valid");
                    log.error("SSL certificate validation failed for {}: certificate not valid until {} - Subject: {} - Check system time synchronization", 
                        hostname, cert.getNotBefore(), cert.getSubjectX500Principal().getName());
                    socket.close();
                    return false;
                }

                // Check hostname verification
                String certHost = cert.getSubjectX500Principal().getName();
                if (!certHost.contains(hostname))
                {
                    String warningMsg = "SSL certificate hostname mismatch: certificate is for " + certHost
                            + ", connecting to " + hostname;
                    warnings.add(warningMsg);
                    recommendations.add("Use the hostname specified in the certificate or disable hostname verification");
                    log.warn("SSL hostname verification warning for {}: certificate subject '{}' does not match hostname", 
                        hostname, certHost);
                }
                else
                {
                    log.debug("SSL hostname verification passed for {}", hostname);
                }
            }

            socket.close();
            log.info("SSL/TLS certificate validation completed successfully for {}:{}", hostname, port);
            return true;
        }
        catch (SSLPeerUnverifiedException e)
        {
            String errorMsg = "SSL peer verification failed: " + e.getMessage();
            errors.add(errorMsg);
            recommendations.add("Install the HPCC cluster's SSL certificate in the Java truststore");
            recommendations.add("Use connection.setAllowInvalidCerts(true) to bypass certificate validation (not recommended for production)");
            log.error("SSL peer verification failed for {}:{} - Certificate is not trusted. " +
                "Install certificate in Java truststore or add to trusted CAs. Error: {}", 
                hostname, port, e.getMessage(), e);
            return false;
        }
        catch (IOException e)
        {
            String errorMsg = "SSL connection failed: " + e.getMessage();
            errors.add(errorMsg);
            recommendations.add("Check SSL/TLS configuration on the HPCC cluster");
            recommendations.add("Verify SSL port " + port + " is correct (default HTTPS port is 18010)");
            
            String exceptionMsg = e.getMessage().toLowerCase();
            if (exceptionMsg.contains("handshake"))
            {
                log.error("SSL connection failed for {}:{}: SSL handshake failure - " +
                    "Cipher suite mismatch or protocol version incompatibility. Error: {}", 
                    hostname, port, e.getMessage(), e);
            }
            else if (exceptionMsg.contains("certificate"))
            {
                log.error("SSL connection failed for {}:{}: Certificate issue - " +
                    "Invalid or untrusted certificate. Error: {}", 
                    hostname, port, e.getMessage(), e);
            }
            else
            {
                log.error("SSL connection failed to {}:{}: {} - Check SSL/TLS configuration", 
                    hostname, port, e.getMessage(), e);
            }
            
            return false;
        }
        catch (Exception e)
        {
            String errorMsg = "Unexpected SSL error: " + e.getMessage();
            errors.add(errorMsg);
            log.error("Unexpected SSL error for {}:{}: {} - {}", hostname, port, 
                e.getClass().getName(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Tests HTTP endpoint availability by attempting a HEAD request.
     * 
     * @param connection the connection to test
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return true if endpoint is accessible, false otherwise
     */
    @WithSpan
    private static boolean testHTTPEndpoint(Connection connection, List<String> errors, List<String> warnings,
            List<String> recommendations)
    {
        try
        {
            URL url = new URL(connection.getBaseUrl());
            log.debug("Testing HTTP endpoint: {} using HEAD request", url);
            
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("HEAD");
            httpConn.setConnectTimeout(connection.getConnectTimeoutMilli());
            httpConn.setReadTimeout(connection.getReadTimeoutMilli());

            // Handle HTTPS with potentially invalid certificates
            if (connection.getIsHttps() && connection.getAllowInvalidCerts())
            {
                log.debug("Configuring SSL to allow invalid certificates for {}", url);
                try
                {
                    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
                    {
                        public X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType)
                        {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType)
                        {
                        }
                    } };

                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    ((HttpsURLConnection) httpConn).setSSLSocketFactory(sc.getSocketFactory());
                    ((HttpsURLConnection) httpConn).setHostnameVerifier((hostname, session) -> true);
                }
                catch (Exception e)
                {
                    log.warn("Failed to configure SSL to allow invalid certs for {}: {}", url, e.getMessage());
                }
            }

            int responseCode = httpConn.getResponseCode();
            log.info("HTTP endpoint test for {}: response code {}", connection.getBaseUrl(), responseCode);

            if (responseCode >= 200 && responseCode < 400)
            {
                log.info("HTTP endpoint is accessible and responding normally: {} (HTTP {})", url, responseCode);
                return true;
            }
            else if (responseCode == 401)
            {
                String warningMsg = "HTTP endpoint requires authentication (401 Unauthorized)";
                warnings.add(warningMsg);
                recommendations.add("Provide valid credentials using connection.setCredentials(username, password)");
                log.warn("HTTP endpoint {} requires authentication (401 Unauthorized) - endpoint is available but needs credentials", url);
                return true; // Endpoint is available, just needs auth
            }
            else if (responseCode == 403)
            {
                String errorMsg = "Access forbidden (403 Forbidden)";
                errors.add(errorMsg);
                recommendations.add("Check if IP address is whitelisted on the HPCC cluster");
                recommendations.add("Verify user has necessary permissions");
                log.error("HTTP endpoint {} access forbidden (403) - IP may not be whitelisted or user lacks permissions", url);
                return false;
            }
            else if (responseCode == 404)
            {
                String errorMsg = "HTTP endpoint not found (404 Not Found)";
                errors.add(errorMsg);
                recommendations.add("Verify the HPCC ESP service is running");
                recommendations.add("Check the connection URL path");
                log.error("HTTP endpoint {} not found (404) - ESP service may not be running or URL path is incorrect", url);
                return false;
            }
            else if (responseCode >= 500)
            {
                String errorMsg = "Server error (HTTP " + responseCode + ")";
                errors.add(errorMsg);
                recommendations.add("Check HPCC cluster logs for errors");
                recommendations.add("Verify the ESP service is running properly");
                log.error("HTTP endpoint {} returned server error (HTTP {}) - check HPCC cluster health and logs", 
                    url, responseCode);
                return false;
            }
            else
            {
                String warningMsg = "Unexpected HTTP response code: " + responseCode;
                warnings.add(warningMsg);
                log.warn("HTTP endpoint {} returned unexpected response code: {} - may indicate service issue", 
                    url, responseCode);
                return false;
            }
        }
        catch (SocketTimeoutException e)
        {
            String errorMsg = "HTTP request timeout";
            errors.add(errorMsg);
            recommendations.add("Increase connection timeout values");
            recommendations.add("Check network latency and congestion");
            log.error("HTTP endpoint test timeout for {} - request exceeded timeout of {} ms. " +
                "Increase timeout or check network/service performance", 
                connection.getBaseUrl(), connection.getReadTimeoutMilli(), e);
            return false;
        }
        catch (IOException e)
        {
            String errorMsg = "HTTP endpoint test failed: " + e.getMessage();
            errors.add(errorMsg);
            recommendations.add("Verify the HPCC ESP service is running and accessible");
            log.error("HTTP endpoint test failed for {}: {} - ESP service may not be running or accessible", 
                connection.getBaseUrl(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Tests authentication by attempting to access a protected resource.
     * 
     * @param connection the connection to test
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return true if authentication succeeds, false otherwise
     */
    @WithSpan
    private static boolean testAuthentication(Connection connection, List<String> errors, List<String> warnings,
            List<String> recommendations)
    {
        try
        {
            // Try to access a protected endpoint that requires authentication
            String authTestResponse = connection.sendGetRequest("esp/getauthtype");

            if (authTestResponse != null && !authTestResponse.isEmpty())
            {
                log.info("Authentication test successful");
                return true;
            }
            else
            {
                warnings.add("Authentication test returned empty response");
                return true; // Empty response might still indicate successful auth
            }
        }
        catch (Exception e)
        {
            String errorMsg = e.getMessage();

            if (errorMsg.contains("401") || errorMsg.contains("Unauthorized"))
            {
                errors.add("Authentication failed: Invalid credentials");
                recommendations.add("Verify username and password are correct");
                recommendations.add("Check if the user account is active and not locked");
                log.error("Authentication failed with 401 Unauthorized");
                return false;
            }
            else if (errorMsg.contains("403") || errorMsg.contains("Forbidden"))
            {
                errors.add("Authorization failed: User lacks necessary permissions");
                recommendations.add("Verify user has required permissions on the HPCC cluster");
                recommendations.add("Check user role assignments");
                log.error("Authentication failed with 403 Forbidden");
                return false;
            }
            else
            {
                warnings.add("Authentication test encountered error: " + errorMsg);
                recommendations.add("Verify credentials are properly encoded");
                log.warn("Authentication test error: {}", errorMsg);
                return true; // Don't fail completely as error might be unrelated to auth
            }
        }
    }

    /**
     * Tests HPCC-specific endpoints used by BaseHPCCWsClient.
     * These are the endpoints that are accessed during client initialization.
     * 
     * @param connection the connection to test
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return true if all HPCC endpoints are accessible, false otherwise
     */
    @WithSpan
    private static boolean testHPCCEndpoints(Connection connection, List<String> errors, List<String> warnings,
            List<String> recommendations)
    {
        boolean allSuccess = true;

        // Test esp/getauthtype endpoint (used by BaseHPCCWsClient.getTargetHPCCAuthenticates)
        if (!testEndpoint(connection, "esp/getauthtype", "ESP authentication type", errors, warnings, recommendations))
        {
            allSuccess = false;
        }

        // Test wssmc/getbuildinfo endpoint (used by BaseHPCCWsClient.getTargetHPCCIsContainerized)
        if (!testEndpoint(connection, "wssmc/getbuildinfo", "WsSMC build info", errors, warnings, recommendations))
        {
            allSuccess = false;
        }

        // Test WsSMC/Activity endpoint (used by BaseHPCCWsClient.getTargetHPCCBuildVersionString)
        if (!testEndpoint(connection, "WsSMC/Activity?rawxml_", "WsSMC activity", errors, warnings, recommendations))
        {
            allSuccess = false;
        }

        return allSuccess;
    }

    /**
     * Tests a specific endpoint by attempting a GET request.
     * 
     * @param connection the connection to test
     * @param endpoint the endpoint path to test
     * @param endpointName friendly name for the endpoint
     * @param errors list to add errors to
     * @param warnings list to add warnings to
     * @param recommendations list to add recommendations to
     * @return true if endpoint is accessible, false otherwise
     */
    @WithSpan
    private static boolean testEndpoint(Connection connection, String endpoint, String endpointName,
            List<String> errors, List<String> warnings, List<String> recommendations)
    {
        log.debug("Testing HPCC endpoint '{}' at: {}/{}", endpointName, connection.getBaseUrl(), endpoint);
        
        try
        {
            String response = connection.sendGetRequest(endpoint);

            if (response == null || response.isEmpty())
            {
                String warningMsg = "HPCC endpoint '" + endpointName + "' (" + endpoint + ") returned empty response";
                warnings.add(warningMsg);
                recommendations.add("Verify the HPCC ESP service is running properly");
                log.warn("HPCC endpoint '{}' ({}) returned empty response - service may not be configured correctly", 
                    endpointName, endpoint);
                return false;
            }

            // Check if response is HTML instead of expected XML
            String trimmedResponse = response.trim();
            if (trimmedResponse.startsWith("<html") || trimmedResponse.startsWith("<!DOCTYPE"))
            {
                String warningMsg = "HPCC endpoint '" + endpointName + "' (" + endpoint + ") returned HTML instead of XML";
                warnings.add(warningMsg);
                recommendations.add("Verify the HPCC ESP service configuration");
                recommendations.add("Check if the endpoint path is correct");
                log.warn("HPCC endpoint '{}' ({}) returned HTML instead of expected XML - " +
                    "endpoint may not be available or incorrectly configured. Response starts with: {}", 
                    endpointName, endpoint, trimmedResponse.substring(0, Math.min(100, trimmedResponse.length())));
                return false;
            }

            log.info("HPCC endpoint '{}' ({}) is accessible and returned valid response ({} bytes)", 
                endpointName, endpoint, response.length());
            return true;
        }
        catch (Exception e)
        {
            String errorMsg = e.getMessage();

            if (errorMsg.contains("404") || errorMsg.contains("Not Found"))
            {
                String error = "HPCC endpoint '" + endpointName + "' (" + endpoint + ") not found (404)";
                errors.add(error);
                recommendations.add("Verify the HPCC ESP service is running");
                recommendations.add("Check if the HPCC version supports this endpoint");
                log.error("HPCC endpoint '{}' ({}) not found (404) - " +
                    "ESP service may not be running or HPCC version may not support this endpoint. " +
                    "This endpoint is used by BaseHPCCWsClient during initialization", 
                    endpointName, endpoint);
                return false;
            }
            else if (errorMsg.contains("401") || errorMsg.contains("Unauthorized"))
            {
                String warningMsg = "HPCC endpoint '" + endpointName + "' (" + endpoint + ") requires authentication";
                warnings.add(warningMsg);
                recommendations.add("Provide valid credentials if the HPCC cluster requires authentication");
                log.warn("HPCC endpoint '{}' ({}) requires authentication (401) - " +
                    "provide credentials if cluster is secured", 
                    endpointName, endpoint);
                return true; // Endpoint exists, just needs auth
            }
            else if (errorMsg.contains("403") || errorMsg.contains("Forbidden"))
            {
                String error = "HPCC endpoint '" + endpointName + "' (" + endpoint + ") access forbidden";
                errors.add(error);
                recommendations.add("Check if IP address is whitelisted on the HPCC cluster");
                recommendations.add("Verify user permissions for this endpoint");
                log.error("HPCC endpoint '{}' ({}) access forbidden (403) - " +
                    "IP may not be whitelisted or user lacks required permissions. " +
                    "This will prevent BaseHPCCWsClient from initializing properly", 
                    endpointName, endpoint);
                return false;
            }
            else if (errorMsg.contains("500") || errorMsg.contains("Internal Server Error"))
            {
                String error = "HPCC endpoint '" + endpointName + "' (" + endpoint + ") server error";
                errors.add(error);
                recommendations.add("Check HPCC cluster logs for internal errors");
                recommendations.add("Verify the ESP service is healthy");
                log.error("HPCC endpoint '{}' ({}) returned server error (500) - " +
                    "check HPCC cluster health and logs. " +
                    "This will cause BaseHPCCWsClient initialization to fail", 
                    endpointName, endpoint);
                return false;
            }
            else
            {
                String warningMsg = "HPCC endpoint '" + endpointName + "' (" + endpoint + ") test failed: " + errorMsg;
                warnings.add(warningMsg);
                recommendations.add("Verify the HPCC ESP service is running and accessible");
                log.warn("HPCC endpoint '{}' ({}) test failed: {} - " +
                    "This may impact BaseHPCCWsClient initialization", 
                    endpointName, endpoint, errorMsg);
                return false;
            }
        }
    }
}
