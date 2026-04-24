package com.g04.cityfix.domain;

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
 * Implementation of LLMService using Hugging Face Inference API
 * Handles communication with language models through the HF Inference API
 * @auther u7901628 Sonia Lin
 */
public class HFInferenceProviderLLMService implements LLMService {
    private static final String TAG = "LLM";
    private final OkHttpClient client;
    private final String apiUrl;
    private final String token;
    private final String model;
    private final String provider;

    public HFInferenceProviderLLMService(String apiUrl, String token, String model, String provider) {
        this.client = new OkHttpClient();
        this.apiUrl = apiUrl;
        this.token = token;
        this.model = model;
        this.provider = provider;
    }

    /**
     * Sends a question to the language model and receives the response
     * @param question The user's question text
     * @param callback Callback to handle success or failure responses
     */
    @Override
    public void askQuestion(String question, LLMCallback callback) {
        MediaType mediaType = MediaType.parse("application/json");

        // Build request body
        JSONObject requestBody = new JSONObject();
        try {
            // Create messages array
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.put(userMessage);

            // Set request parameters
            requestBody.put("messages", messages);
            requestBody.put("model", model);
            requestBody.put("provider", provider);
            requestBody.put("stream", false);

        } catch (JSONException e) {
            callback.onFailure("The construction of the request body failed: " + e.getMessage());
            return;
        }

        // Create HTTP request with headers
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        // Create HTTP request with headers
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Internet request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

                if (response.isSuccessful()) {
                    try {
                        // Parse the response
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray choices = jsonObject.getJSONArray("choices");
                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject message = firstChoice.getJSONObject("message");
                        String content = message.getString("content");

                        callback.onSuccess(content);
                    } catch (JSONException e) {
                        callback.onFailure("Failed to parse JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("API error: " + response.code() + " - " + responseData);
                }
            }
        });
    }
}
