package com.g04.cityfix.domain;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of LLMService using Google's Gemini API
 * Handles communication with Gemini language model
 * @auther u7901628 Sonia Lin
 */
public class GeminiLLMService implements LLMService {
    private static final String TAG = "GeminiLLM";
    private final OkHttpClient client;
    private final String apiUrl;
    private final String apiKey;

    public GeminiLLMService(String apiUrl, String apiKey) {
        this.client = new OkHttpClient();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * Sends a question to the Gemini model and receives the response
     * @param question The user's question text
     * @param callback Callback to handle success or failure responses
     */
    @Override
    public void askQuestion(String question, LLMCallback callback) {
        MediaType mediaType = MediaType.parse("application/json");

        // Gemini expects a "contents" array containing "parts"
        String json = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\"text\": \"" + question.replace("\"", "\\\"") + "\"}\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Log.d(TAG, "Request: " + json);

        // Build URL with API key
        String fullUrl = apiUrl + "?key=" + apiKey;

        // Create HTTP request with headers
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(fullUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // Create HTTP request with headers
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API request failed", e);
                callback.onFailure("internet request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response body: " + responseData);

                if (response.isSuccessful()) {
                    try {
                        // Parse Gemini API response format
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray candidates = jsonObject.getJSONArray("candidates");
                        JSONObject firstCandidate = candidates.getJSONObject(0);
                        JSONObject content = firstCandidate.getJSONObject("content");
                        JSONArray parts = content.getJSONArray("parts");
                        String generatedText = parts.getJSONObject(0).getString("text");

                        callback.onSuccess(generatedText);
                    } catch (JSONException e) {
                        Log.e(TAG, "parse response failed", e);
                        callback.onFailure("parse response failed: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("API error: " + response.code() + " - " + responseData);
                }
            }
        });
    }
}
