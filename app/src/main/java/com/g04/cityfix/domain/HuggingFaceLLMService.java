package com.g04.cityfix.domain;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * HuggingFace LLM Service implementation
 * @auther u7901628 Sonia Lin
 */
public class HuggingFaceLLMService implements LLMService {
    private static final String TAG = "LLM";
    private final OkHttpClient client;
    private final String apiUrl;
    private final String token;

    /**
     * @param apiUrl HuggingFace API URL
     * @param token HuggingFace API Token
     */
    public HuggingFaceLLMService(String apiUrl, String token) {
        this.client = new OkHttpClient();
        this.apiUrl = apiUrl;
        this.token = token;
    }

    @Override
    public void askQuestion(String inputText, LLMCallback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        String json = "{ \"inputs\": \"" + inputText + "\" }";
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API request failed", e);
                callback.onFailure("Internet request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        JSONObject firstItem = jsonArray.getJSONObject(0);
                        String generatedText = firstItem.getString("generated_text");
                        Log.d(TAG, "Response: " + generatedText);
                        callback.onSuccess(generatedText);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse JSON: " + e.getMessage());
                        callback.onFailure("Failed to parse JSON: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Error: " + response.code());
                    callback.onFailure("API error: " + response.code());
                }
            }
        });
    }
}