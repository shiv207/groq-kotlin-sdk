package com.indiedev.groqsdk

import com.indiedev.groqsdk.config.GroqConfig
import com.indiedev.groqsdk.config.GroqModel
import com.indiedev.groqsdk.exceptions.*
import com.indiedev.groqsdk.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

/**
 * Main client for interacting with the Groq API
 */
class GroqClient(private val config: GroqConfig) {
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = config.timeoutMillis
            connectTimeoutMillis = config.timeoutMillis
            socketTimeoutMillis = config.timeoutMillis
        }
        
        if (config.enableLogging) {
            install(Logging) {
                level = LogLevel.INFO
            }
        }
        
        defaultRequest {
            header("Authorization", "Bearer ${config.apiKey}")
            header("Content-Type", "application/json")
        }
    }
    
    /**
     * Creates a chat completion using the specified model and messages
     */
    suspend fun createChatCompletion(
        model: GroqModel,
        messages: List<ChatMessage>,
        temperature: Double? = null,
        maxTokens: Int? = null,
        topP: Double? = null
    ): ChatCompletionResponse {
        return createChatCompletion(
            ChatCompletionRequest(
                model = model.modelId,
                messages = messages,
                temperature = temperature,
                maxTokens = maxTokens,
                topP = topP
            )
        )
    }
    
    /**
     * Creates a chat completion with a simple string message
     */
    suspend fun createChatCompletion(
        model: GroqModel,
        message: String,
        temperature: Double? = null,
        maxTokens: Int? = null,
        topP: Double? = null
    ): ChatCompletionResponse {
        return createChatCompletion(
            model = model,
            messages = listOf(ChatMessage(MessageRole.USER, message)),
            temperature = temperature,
            maxTokens = maxTokens,
            topP = topP
        )
    }
    
    /**
     * Creates a chat completion with full request customization
     */
    suspend fun createChatCompletion(request: ChatCompletionRequest): ChatCompletionResponse {
        validateRequest(request)
        
        return executeWithRetry {
            try {
                val response = httpClient.post("${config.baseUrl}/chat/completions") {
                    setBody(request)
                }
                
                when (response.status) {
                    HttpStatusCode.OK -> response.body<ChatCompletionResponse>()
                    HttpStatusCode.Unauthorized -> throw GroqAuthenticationException()
                    HttpStatusCode.TooManyRequests -> {
                        val retryAfter = response.headers["retry-after"]?.toLongOrNull()
                        throw GroqRateLimitException(retryAfterSeconds = retryAfter)
                    }
                    else -> {
                        val errorBody = try {
                            response.body<GroqError>()
                        } catch (e: Exception) {
                            throw GroqApiException(
                                statusCode = response.status.value,
                                message = response.bodyAsText()
                            )
                        }
                        throw GroqApiException(
                            statusCode = response.status.value,
                            message = errorBody.error.message,
                            errorCode = errorBody.error.code
                        )
                    }
                }
            } catch (e: GroqException) {
                throw e
            } catch (e: HttpRequestTimeoutException) {
                throw GroqTimeoutException(cause = e)
            } catch (e: Exception) {
                throw GroqNetworkException(cause = e)
            }
        }
    }
    
    /**
     * Convenience method to get just the text response
     */
    suspend fun generateText(
        model: GroqModel,
        prompt: String,
        temperature: Double? = null,
        maxTokens: Int? = null
    ): String {
        val response = createChatCompletion(
            model = model,
            message = prompt,
            temperature = temperature,
            maxTokens = maxTokens
        )
        return response.choices.firstOrNull()?.message?.content 
            ?: throw GroqParsingException("No response content found")
    }
    
    private suspend fun <T> executeWithRetry(operation: suspend () -> T): T {
        var lastException: GroqException? = null
        
        repeat(config.retryAttempts + 1) { attempt ->
            try {
                return operation()
            } catch (e: GroqRateLimitException) {
                lastException = e
                if (attempt < config.retryAttempts) {
                    val delayMs = e.retryAfterSeconds?.times(1000) ?: (1000L * (attempt + 1))
                    delay(delayMs)
                }
            } catch (e: GroqNetworkException) {
                lastException = e
                if (attempt < config.retryAttempts) {
                    delay(1000L * (attempt + 1)) // Exponential backoff
                }
            } catch (e: GroqException) {
                throw e // Don't retry other exceptions
            }
        }
        
        throw lastException ?: GroqNetworkException("Max retry attempts exceeded")
    }
    
    private fun validateRequest(request: ChatCompletionRequest) {
        if (request.messages.isEmpty()) {
            throw GroqValidationException("Messages cannot be empty")
        }
        
        if (request.temperature != null && (request.temperature < 0 || request.temperature > 2)) {
            throw GroqValidationException("Temperature must be between 0 and 2")
        }
        
        if (request.topP != null && (request.topP < 0 || request.topP > 1)) {
            throw GroqValidationException("Top P must be between 0 and 1")
        }
        
        if (request.maxTokens != null && request.maxTokens < 1) {
            throw GroqValidationException("Max tokens must be positive")
        }
    }
    
    /**
     * Close the HTTP client
     */
    fun close() {
        httpClient.close()
    }
    
    companion object {
        /**
         * Creates a GroqClient with the given API key using default configuration
         */
        fun create(apiKey: String): GroqClient {
            return GroqClient(GroqConfig.Builder().apiKey(apiKey).build())
        }
        
        /**
         * Creates a GroqClient with custom configuration
         */
        fun create(configBuilder: GroqConfig.Builder.() -> Unit): GroqClient {
            return GroqClient(GroqConfig.Builder().apply(configBuilder).build())
        }
    }
}
