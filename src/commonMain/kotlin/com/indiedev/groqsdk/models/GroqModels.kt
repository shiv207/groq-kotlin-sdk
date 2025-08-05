package com.indiedev.groqsdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a chat message in the conversation
 */
@Serializable
data class ChatMessage(
    val role: MessageRole,
    val content: String
)

/**
 * Message roles in the conversation
 */
@Serializable
enum class MessageRole(val value: String) {
    @SerialName("system")
    SYSTEM("system"),
    
    @SerialName("user") 
    USER("user"),
    
    @SerialName("assistant")
    ASSISTANT("assistant")
}

/**
 * Request payload for chat completions
 */
@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double? = null,
    @SerialName("max_tokens")
    val maxTokens: Int? = null,
    @SerialName("top_p")
    val topP: Double? = null,
    val stream: Boolean = false
)

/**
 * Response from chat completions API
 */
@Serializable
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

@Serializable
data class Choice(
    val index: Int,
    val message: ChatMessage,
    @SerialName("finish_reason")
    val finishReason: String?
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens") 
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

/**
 * Error response from the API
 */
@Serializable
data class GroqError(
    val error: ErrorDetails
)

@Serializable
data class ErrorDetails(
    val message: String,
    val type: String,
    val code: String?
)
