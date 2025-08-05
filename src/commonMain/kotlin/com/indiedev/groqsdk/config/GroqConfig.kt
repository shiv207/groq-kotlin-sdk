package com.indiedev.groqsdk.config

/**
 * Configuration for the Groq SDK
 */
data class GroqConfig(
    val apiKey: String,
    val baseUrl: String = DEFAULT_BASE_URL,
    val timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS,
    val enableLogging: Boolean = false,
    val retryAttempts: Int = 3
) {
    companion object {
        const val DEFAULT_BASE_URL = "https://api.groq.com/openai/v1"
        const val DEFAULT_TIMEOUT_MILLIS = 30_000L
    }
    
    /**
     * Builder for GroqConfig
     */
    class Builder {
        private var apiKey: String = ""
        private var baseUrl: String = DEFAULT_BASE_URL
        private var timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS
        private var enableLogging: Boolean = false
        private var retryAttempts: Int = 3
        
        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun baseUrl(baseUrl: String) = apply { this.baseUrl = baseUrl }
        fun timeout(timeoutMillis: Long) = apply { this.timeoutMillis = timeoutMillis }
        fun enableLogging(enable: Boolean = true) = apply { this.enableLogging = enable }
        fun retryAttempts(attempts: Int) = apply { this.retryAttempts = attempts }
        
        fun build(): GroqConfig {
            require(apiKey.isNotBlank()) { "API key is required" }
            require(retryAttempts >= 0) { "Retry attempts must be non-negative" }
            require(timeoutMillis > 0) { "Timeout must be positive" }
            
            return GroqConfig(
                apiKey = apiKey,
                baseUrl = baseUrl,
                timeoutMillis = timeoutMillis,
                enableLogging = enableLogging,
                retryAttempts = retryAttempts
            )
        }
    }
}

/**
 * Available Groq models
 */
enum class GroqModel(val modelId: String) {
    LLAMA_3_1_405B_REASONING("llama-3.1-405b-reasoning"),
    LLAMA_3_1_70B_VERSATILE("llama-3.1-70b-versatile"),
    LLAMA_3_1_8B_INSTANT("llama-3.1-8b-instant"),
    LLAMA_3_2_11B_VISION_PREVIEW("llama-3.2-11b-vision-preview"),
    LLAMA_3_2_1B_PREVIEW("llama-3.2-1b-preview"),
    LLAMA_3_2_3B_PREVIEW("llama-3.2-3b-preview"),
    MIXTRAL_8X7B_32768("mixtral-8x7b-32768"),
    GEMMA_7B_IT("gemma-7b-it"),
    GEMMA2_9B_IT("gemma2-9b-it");
    
    override fun toString(): String = modelId
}
