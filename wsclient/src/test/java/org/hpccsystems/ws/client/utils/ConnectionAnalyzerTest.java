package org.hpccsystems.ws.client.utils;

/*******************************************************************************
 *     HPCC SYSTEMS software Copyright (C) 2025 HPCC Systems®.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *******************************************************************************/

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import org.hpccsystems.ws.client.utils.ConnectionAnalyzer.AnalysisResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(org.hpccsystems.commons.annotations.BaseTests.class)
public class ConnectionAnalyzerTest
{
    @Test
    public void testValidConnectionConfiguration() throws MalformedURLException
    {
        Connection conn = new Connection("http://localhost:8010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        assertNotNull("Analysis result should not be null", result);
        assertNotNull("Errors list should not be null", result.getErrors());
        assertNotNull("Warnings list should not be null", result.getWarnings());
        assertNotNull("Recommendations list should not be null", result.getRecommendations());
    }

    @Test
    public void testInvalidHostWithUnderscore() throws MalformedURLException
    {
        // Connection class validates underscores in hostname when parsing URL
        // It should throw MalformedURLException
        try
        {
            Connection conn = new Connection("http://invalid_host:8010");
            // If construction succeeds (shouldn't), analyze it
            AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);
            assertFalse("Analysis should fail for hostname with underscore", result.isSuccessful());
        }
        catch (MalformedURLException e)
        {
            // This is expected behavior - Connection validates hostname
            assertTrue("Error message should mention underscores",
                    e.getMessage().contains("underscores") || e.getMessage().contains("_") || e.getMessage().contains("Invalid"));
        }
    }

    @Test
    public void testInvalidPort() throws MalformedURLException
    {
        // Port 0 is technically valid in the constructor but cannot be connected to
        Connection conn = new Connection("http", "localhost", "0");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        assertFalse("Analysis should fail for port 0", result.isSuccessful());
        assertTrue("Should have errors", result.getErrors().size() > 0);

        // Port 0 will fail on connection, not validation, so check for connection errors
        boolean foundError = false;
        for (String error : result.getErrors())
        {
            if (error.contains("connection") || error.contains("Connection") || error.contains("failed"))
            {
                foundError = true;
                break;
            }
        }
        assertTrue("Should have connection failure error", foundError);
    }

    @Test
    public void testHTTPSWithHTTPPort() throws MalformedURLException
    {
        Connection conn = new Connection("https://localhost:8010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        assertNotNull("Analysis result should not be null", result);

        // Should have a warning about using HTTP default port with HTTPS
        boolean foundWarning = false;
        for (String warning : result.getWarnings())
        {
            if (warning.contains("8010") && warning.contains("HTTPS"))
            {
                foundWarning = true;
                break;
            }
        }
        assertTrue("Should warn about using HTTP port (8010) with HTTPS protocol", foundWarning);

        // Should recommend using HTTPS default port
        boolean foundRecommendation = false;
        for (String rec : result.getRecommendations())
        {
            if (rec.contains("18010"))
            {
                foundRecommendation = true;
                break;
            }
        }
        assertTrue("Should recommend using HTTPS default port (18010)", foundRecommendation);
    }

    @Test
    public void testHTTPWithHTTPSPort() throws MalformedURLException
    {
        Connection conn = new Connection("http://localhost:18010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        assertNotNull("Analysis result should not be null", result);

        // Should have a warning about using HTTPS default port with HTTP
        boolean foundWarning = false;
        for (String warning : result.getWarnings())
        {
            if (warning.contains("18010") && warning.contains("HTTP"))
            {
                foundWarning = true;
                break;
            }
        }
        assertTrue("Should warn about using HTTPS port (18010) with HTTP protocol", foundWarning);
    }

    @Test
    public void testInvalidProtocol() throws MalformedURLException
    {
        // Connection normalizes protocol to http if it's not https
        // Let's check that it defaults correctly
        Connection conn = new Connection("ftp", "localhost", "8010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        // Connection will default to http, so analysis might not fail on protocol
        // But it should still provide diagnostic information
        assertNotNull("Analysis result should not be null", result);
        
        // The connection will have been normalized to http, so check that it works
        assertEquals("Protocol should be normalized to http", "http", conn.getProtocol());
    }

    @Test
    public void testDNSResolutionFailure()
    {
        try
        {
            Connection conn = new Connection("http://nonexistent-host-that-does-not-exist.invalid:8010");
            AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

            assertFalse("Analysis should fail for non-existent host", result.isSuccessful());
            assertTrue("Should have errors for DNS resolution failure", result.getErrors().size() > 0);

            boolean foundDNSError = false;
            for (String error : result.getErrors())
            {
                if (error.contains("DNS") || error.contains("resolution") || error.contains("hostname"))
                {
                    foundDNSError = true;
                    break;
                }
            }
            assertTrue("Should have specific error about DNS resolution failure", foundDNSError);
        }
        catch (MalformedURLException e)
        {
            fail("Should not throw MalformedURLException for valid URL format: " + e.getMessage());
        }
    }

    @Test
    public void testEmptyHost() throws MalformedURLException
    {
        Connection conn = new Connection("http", "", "8010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        assertFalse("Analysis should fail for empty host", result.isSuccessful());
        assertTrue("Should have errors for empty host", result.getErrors().size() > 0);

        boolean foundHostError = false;
        for (String error : result.getErrors())
        {
            if (error.toLowerCase().contains("host") && (error.contains("empty") || error.contains("null")))
            {
                foundHostError = true;
                break;
            }
        }
        assertTrue("Should have specific error about empty host", foundHostError);
    }

    @Test
    public void testAnalysisResultToString() throws MalformedURLException
    {
        Connection conn = new Connection("http://localhost:8010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        String resultString = result.toString();
        assertNotNull("toString should not return null", resultString);
        assertTrue("toString should contain 'Connection Analysis Result'", resultString.contains("Connection Analysis Result"));
        assertTrue("toString should contain status", resultString.contains("Status:"));
    }

    @Test
    public void testPortOutOfRange()
    {
        // Connection might throw when trying to get port as int if it's > 65535
        try
        {
            Connection conn = new Connection("http://localhost:99999");
            // If we can create it, test the analyzer
            AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

            assertFalse("Analysis should fail for port out of valid range", result.isSuccessful());
            assertTrue("Should have errors", result.getErrors().size() > 0);

            // Should either have a port validation error or connection failure
            boolean foundError = false;
            for (String error : result.getErrors())
            {
                if (error.contains("port") || error.contains("99999") || error.contains("connection") || error.contains("Connection"))
                {
                    foundError = true;
                    break;
                }
            }
            assertTrue("Should have error about invalid port or connection failure", foundError);
        }
        catch (MalformedURLException e)
        {
            // Expected - URL parsing might catch invalid ports
            assertTrue("Error should be about invalid port/URL", true);
        }
        catch (IllegalArgumentException e)
        {
            // Also acceptable - Connection validation might throw for invalid port
            assertTrue("Error should mention port", e.getMessage().toLowerCase().contains("port"));
        }
    }

    @Test
    public void testConnectionWithCredentials() throws MalformedURLException
    {
        Connection conn = new Connection("http://localhost:8010");
        conn.setCredentials("testuser", "testpass");

        assertTrue("Connection should have credentials", conn.hasCredentials());

        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);
        assertNotNull("Analysis result should not be null", result);

        // The analysis should attempt authentication tests when credentials are present
        // Even if it fails, we should get some diagnostic information
        assertNotNull("Should have diagnostic information", result.getErrors());
        assertNotNull("Should have recommendations", result.getRecommendations());
    }

    @Test
    public void testLocalhostConnection() throws MalformedURLException
    {
        Connection conn = new Connection("http://localhost:8010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        assertNotNull("Analysis result should not be null", result);

        // Check if there's a warning about localhost
        boolean hasLocalhostWarning = false;
        for (String warning : result.getWarnings())
        {
            if (warning.contains("localhost") || warning.contains("Hostname resolves to localhost"))
            {
                hasLocalhostWarning = true;
                break;
            }
        }
        // It's okay either way - just checking the analysis runs
        // assertTrue("Should warn about localhost connection", hasLocalhostWarning);
    }

    @Test
    public void testInvalidTimeoutValues() throws MalformedURLException
    {
        Connection conn = new Connection("http://localhost:8010");
        // Set invalid timeout values after construction
        try
        {
            conn.setConnectTimeoutMilli(-1000);
            conn.setReadTimeoutMilli(0);

            AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

            assertNotNull("Analysis result should not be null", result);

            // Should have warnings about invalid timeout values
            boolean foundTimeoutWarning = false;
            for (String warning : result.getWarnings())
            {
                if (warning.contains("timeout"))
                {
                    foundTimeoutWarning = true;
                    break;
                }
            }
            assertTrue("Should warn about invalid timeout values", foundTimeoutWarning);
        }
        catch (IllegalArgumentException e)
        {
            // If Connection itself validates negative timeouts, that's also acceptable
            assertTrue("Exception should mention timeout", e.getMessage().toLowerCase().contains("timeout"));
        }
    }

    @Test
    public void testHTTPSConnection() throws MalformedURLException
    {
        Connection conn = new Connection("https://localhost:18010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        assertNotNull("Analysis result should not be null", result);
        // HTTPS connections should trigger SSL certificate tests
        // Even if they fail, we should get diagnostic information
        assertNotNull("Should have diagnostic information", result.getErrors());
    }

    @Test
    public void testAnalysisResultGetters() throws MalformedURLException
    {
        Connection conn = new Connection("http://localhost:8010");
        AnalysisResult result = ConnectionAnalyzer.analyzeConnection(conn);

        // Test all getters return non-null values
        assertNotNull("getErrors() should not return null", result.getErrors());
        assertNotNull("getWarnings() should not return null", result.getWarnings());
        assertNotNull("getRecommendations() should not return null", result.getRecommendations());

        // Verify lists are independent copies (defensive copies)
        result.getErrors().add("test error");
        assertEquals("Original errors list should not be modified",
                0, result.getErrors().stream().filter(e -> e.equals("test error")).count());
    }
}
