package com.g04.cityfix.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for HFInferenceProviderLLMService Class
 * @author u7901628 Sonia Lin
 */
@RunWith(MockitoJUnitRunner.class)
public class HFInferenceProviderLLMServiceTest {

    private MockWebServer mockWebServer;
    private HFInferenceProviderLLMService llmService;

    @Before
    public void setUp() throws IOException {
        // Setup mock web server
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Get the base URL for the mock server
        String baseUrl = mockWebServer.url("/").toString();

        // Create LLM service with mock server URL
        llmService = new HFInferenceProviderLLMService(
                baseUrl,
                "test-token",
                "test-model",
                "test-provider");
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void askQuestion_successfulResponse_callsOnSuccess() throws InterruptedException {
        // Prepare successful response
        String successResponse = "{\"choices\":[{\"message\":{\"content\":\"This is a test response.\"}}]}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(successResponse)
                .addHeader("Content-Type", "application/json"));

        // Create a latch to wait for async callback
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] result = new String[1];

        // Execute test
        llmService.askQuestion("What is the capital of France?", new LLMService.LLMCallback() {
            @Override
            public void onSuccess(String response) {
                result[0] = response;
                latch.countDown();
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Should not fail: " + errorMessage);
                latch.countDown();
            }
        });

        // Wait for callback (with timeout)
        assertTrue(latch.await(5, TimeUnit.SECONDS));

        // Verify result
        assertEquals("This is a test response.", result[0]);
    }

    @Test
    public void askQuestion_apiError_callsOnFailure() throws InterruptedException {
        // Prepare error response
        String errorResponse = "{\"error\":\"Invalid API key\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody(errorResponse)
                .addHeader("Content-Type", "application/json"));

        // Create a latch to wait for async callback
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] errorResult = new String[1];

        // Execute test
        llmService.askQuestion("What is the capital of France?", new LLMService.LLMCallback() {
            @Override
            public void onSuccess(String response) {
                fail("Should not succeed");
                latch.countDown();
            }

            @Override
            public void onFailure(String errorMessage) {
                errorResult[0] = errorMessage;
                latch.countDown();
            }
        });

        // Wait for callback (with timeout)
        assertTrue(latch.await(5, TimeUnit.SECONDS));

        // Verify error message contains expected information
        assertTrue(errorResult[0].contains("401"));
        assertTrue(errorResult[0].contains(errorResponse));
    }
}