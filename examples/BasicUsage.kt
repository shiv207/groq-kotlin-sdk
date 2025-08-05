package com.indiedev.groqsdk.examples

import com.indiedev.groqsdk.*
import com.indiedev.groqsdk.config.*
import com.indiedev.groqsdk.models.*
import com.indiedev.groqsdk.exceptions.*
import kotlinx.coroutines.runBlocking

/**
 * Basic usage examples for the Groq SDK
 */
object BasicUsage {
    
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // Replace with your actual API key
        val apiKey = "your-groq-api-key-here"
        
        // Example 1: Simple text generation
        simpleTextGeneration(apiKey)
        
        // Example 2: Chat conversation
        chatConversation(apiKey)
        
        // Example 3: Custom configuration
        customConfiguration(apiKey)
        
        // Example 4: Error handling
        errorHandling(apiKey)
    }
    
    private suspend fun simpleTextGeneration(apiKey: String) {
        println("=== Simple Text Generation ===")
        
        val client = GroqClient.create(apiKey)
        
        try {
            val response = client.generateText(
                model = GroqModel.LLAMA_3_1_8B_INSTANT,
                prompt = "Write a haiku about programming",
                temperature = 0.7
            )
            
            println("Generated text: $response")
        } catch (e: GroqException) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }
    
    private suspend fun chatConversation(apiKey: String) {
        println("\n=== Chat Conversation ===")
        
        val client = GroqClient.create(apiKey)
        
        val messages = listOf(
            ChatMessage(MessageRole.SYSTEM, "You are a helpful coding assistant."),
            ChatMessage(MessageRole.USER, "How do I create a REST API in Kotlin?")
        )
        
        try {
            val response = client.createChatCompletion(
                model = GroqModel.LLAMA_3_1_70B_VERSATILE,
                messages = messages,
                temperature = 0.3,
                maxTokens = 500
            )
            
            val assistantMessage = response.choices.first().message
            println("Assistant: ${assistantMessage.content}")
            println("Tokens used: ${response.usage.totalTokens}")
            
        } catch (e: GroqException) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }
    
    private suspend fun customConfiguration(apiKey: String) {
        println("\n=== Custom Configuration ===")
        
        val client = GroqClient.create {
            apiKey(apiKey)
            enableLogging(true)
            timeout(60_000L) // 60 seconds
            retryAttempts(5)
        }
        
        try {
            val response = client.generateText(
                model = GroqModel.MIXTRAL_8X7B_32768,
                prompt = "Explain the benefits of Kotlin Multiplatform",
                maxTokens = 300
            )
            
            println("Response: $response")
        } catch (e: GroqException) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }
    
    private suspend fun errorHandling(apiKey: String) {
        println("\n=== Error Handling ===")
        
        val client = GroqClient.create(apiKey)
        
        try {
            // This will likely fail due to invalid parameters to demonstrate error handling
            val response = client.createChatCompletion(
                model = GroqModel.LLAMA_3_1_8B_INSTANT,
                messages = emptyList(), // Invalid: empty messages
                temperature = 3.0 // Invalid: temperature > 2
            )
            
            println("Response: ${response.choices.first().message.content}")
            
        } catch (e: GroqValidationException) {
            println("Validation error: ${e.message}")
        } catch (e: GroqAuthenticationException) {
            println("Authentication error: ${e.message}")
        } catch (e: GroqRateLimitException) {
            println("Rate limit exceeded. Retry after: ${e.retryAfterSeconds} seconds")
        } catch (e: GroqApiException) {
            println("API error ${e.statusCode}: ${e.message}")
        } catch (e: GroqNetworkException) {
            println("Network error: ${e.message}")
        } catch (e: GroqTimeoutException) {
            println("Request timed out: ${e.message}")
        } catch (e: GroqException) {
            println("General Groq error: ${e.message}")
        } finally {
            client.close()
        }
    }
}
