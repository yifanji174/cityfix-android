package com.g04.cityfix.domain;

import android.content.Context;
import com.g04.cityfix.R;

/**
 * Factory design pattern to adapt to different LLM model
 * @auther u7901628 Sonia Lin
 */
public class LLMServiceFactory {
    /**
     * Create HuggingFace LLM Service
     * @return LLMService
     */
    public static LLMService createHuggingFaceLLMService(Context context) {
        return new HuggingFaceLLMService(context.getString(R.string.huggingface_API_url), "Bearer " + context.getString(R.string.huggingface_token));
    }

    /**
     * Create Gemini LLM Service
     * @return LLMService
     */
    public static LLMService createGeminiLLMService(Context context) {
        return new GeminiLLMService(context.getString(R.string.gemini_api_url), context.getString(R.string.gemini_api_key));
    }

    /**
     * Create HuggingFace Inference Providers LLM Service
     * @return LLMService
     */
    public static LLMService createHFInferenceProviderLLMService(Context context) {
        return new HFInferenceProviderLLMService(context.getString(R.string.hf_inference_providers_url), context.getString(R.string.huggingface_token),
                context.getString(R.string.llm_model), context.getString(R.string.llm_provider));
    }

    /**
     * In the future, the creation methods of other LLM services can be added
     * Currently, only the HFInferenceProviderLLMService works,
     * HuggingFaceLLMService returns 404,
     * GeminiLLMService gemini-1.0-pro is not found for API version v1beta.
     */
}
