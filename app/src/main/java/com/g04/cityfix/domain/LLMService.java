package com.g04.cityfix.domain;

/**
 * @auther u7901628 Sonia Lin
 */
public interface LLMService {
    /**
     * Send question to LLM and requests response
     * @param question
     * @param callback
     */
    void askQuestion(String question, LLMCallback callback);

    /**
     * LLM response callback interface
     */
    interface LLMCallback {
        void onSuccess(String answer);
        void onFailure(String errorMessage);
    }
}
