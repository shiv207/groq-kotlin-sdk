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
 * 
 * Note: Preview models may be discontinued at short notice. For production use, prefer models marked as production-ready.
 */
enum class GroqModel(val modelId: String) {
    // Production Models
    /**
     * Llama 3.3 70B Versatile - Production-ready model for general purpose use
     */
    LLAMA_3_3_70B_VERSATILE("llama-3.3-70b-versatile"),
    
    /**
     * Llama 3.1 8B Instant - Fast, efficient model for quick responses
     */
    LLAMA_3_1_8B_INSTANT("llama-3.1-8b-instant"),
    
    /**
     * Gemma 2 9B IT - Lightweight, efficient model for code and chat
     */
    GEMMA2_9B_IT("gemma2-9b-it"),
    
    // Preview Models
    /**
     * Kimi K2 Instruct - Moonshot AI's Mixture-of-Experts model with 1T total parameters (32B active)
     * Note: Preview model - not for production use
     */
    KIMI_K2_INSTRUCT("kimi-k2-instruct"),
    
    /**
     * Qwen 3 32B - Latest generation with advanced reasoning and multilingual support
     * Note: Preview model - not for production use
     */
    QWEN_3_32B("qwen-3-32b"),
    
    /**
     * Llama 3.1 70B Versatile - General purpose model
     * Note: Preview model - not for production use
     */
    LLAMA_3_1_70B_VERSATILE("llama-3.1-70b-versatile"),
    
    /**
     * Llama 3.2 11B Vision - Multimodal model with vision capabilities
     * Note: Preview model - not for production use
     */
    LLAMA_3_2_11B_VISION_PREVIEW("llama-3.2-11b-vision-preview"),
    
    /**
     * Mixtral 8x7B - Large context window model (32k tokens)
     * Note: Preview model - not for production use
     */
    MIXTRAL_8X7B_32768("mixtral-8x7b-32768"),
    
    /**
     * DeepSeek R1 Distill Llama 70B - High-performance reasoning model
     * Note: Preview model - not for production use
     */
    DEEPSEEK_R1_DISTILL_LLAMA_70B("deepseek-r1-distill-llama-70b"),
    
    /**
     * Llama 4 Maverick 17B - Specialized for complex tasks
     * Note: Preview model - not for production use
     */
    LLAMA_4_MAVERICK_17B("llama-4-maverick-17b-128e-instruct"),
    
    /**
     * Llama 4 Scout 17B - Optimized for specific use cases
     * Note: Preview model - not for production use
     */
    LLAMA_4_SCOUT_17B("llama-4-scout-17b-16e-instruct");
    
    /**
     * Returns the model ID string used in API requests
     */
    override fun toString(): String = modelId
    
    companion object {
        /**
         * Get all production-ready models
         */
        fun productionModels(): List<GroqModel> = listOf(
            LLAMA_3_3_70B_VERSATILE,
            LLAMA_3_1_8B_INSTANT,
            GEMMA2_9B_IT
        )
        
        /**
         * Get all preview models
         */
        fun previewModels(): List<GroqModel> = listOf(
            KIMI_K2_INSTRUCT,
            QWEN_3_32B,
            LLAMA_3_1_70B_VERSATILE,
            LLAMA_3_2_11B_VISION_PREVIEW,
            MIXTRAL_8X7B_32768,
            DEEPSEEK_R1_DISTILL_LLAMA_70B,
            LLAMA_4_MAVERICK_17B,
            LLAMA_4_SCOUT_17B
        )
    }
}
