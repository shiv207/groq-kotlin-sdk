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
            // Using the latest production model
            val response = client.generateText(
                model = GroqModel.LLAMA_3_3_70B_VERSATILE,
                prompt = "Write a haiku about artificial intelligence",
                temperature = 0.7
            )
            println("Generated text (70B): ${response.choices.first().message.content}")
            
            // Example with a preview model (not for production)
            val previewResponse = client.generateText(
                model = GroqModel.KIMI_K2_INSTRUCT,
                prompt = "Explain quantum computing in simple terms",
                temperature = 0.3
            )
            println("\nPreview model response: ${previewResponse.choices.first().message.content}")
            
        } catch (e: GroqException) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }
    
    private suspend fun chatConversation(apiKey: String) {
        println("\n=== Chat Conversation ===")
        
        val client = GroqClient.create(apiKey)
        
        // Example 1: Basic chat with production model
        val basicMessages = listOf(
            ChatMessage(MessageRole.SYSTEM, "You are a helpful coding assistant."),
            ChatMessage(MessageRole.USER, "How do I create a REST API in Kotlin using Ktor?")
        )
        
        try {
            println("\n--- Basic Chat Example ---")
            val response = client.createChatCompletion(
                model = GroqModel.LLAMA_3_3_70B_VERSATILE,
                messages = basicMessages,
                temperature = 0.3,
                maxTokens = 500
            )
            
            val assistantMessage = response.choices.first().message
            println("Assistant: ${assistantMessage.content}")
            println("Tokens used: ${response.usage.totalTokens}")
            
            // Example 2: Using a preview model with vision capabilities
            println("\n--- Vision Model Example (Preview) ---")
            val visionMessages = listOf(
                ChatMessage(
                    role = MessageRole.SYSTEM, 
                    content = "You are an AI assistant that can understand images."
                ),
                ChatMessage(
                    role = MessageRole.USER,
                    content = "What's in this image?",
                    images = listOf("https://example.com/sample-image.jpg") // Replace with actual image URL
                )
            )
            
            val visionResponse = client.createChatCompletion(
                model = GroqModel.LLAMA_3_2_11B_VISION_PREVIEW,
                messages = visionMessages,
                maxTokens = 300
            )
            
            println("Vision model response: ${visionResponse.choices.first().message.content}")
            
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
            // Example with custom parameters using a production model
            val response = client.generateText(
                model = GroqModel.LLAMA_3_3_70B_VERSATILE,
                prompt = "Compare Kotlin Multiplatform with Flutter and React Native",
                maxTokens = 400,
                temperature = 0.7,
                topP = 0.9,
                n = 2,  // Get 2 completions
                stop = listOf("\n"),
                presencePenalty = 0.6,
                frequencyPenalty = 0.5
            )
            
            println("Generated completions:")
            response.choices.forEachIndexed { index, choice ->
                println("\n--- Completion ${index + 1} ---")
                println(choice.message.content)
            }
            
            // Example of using the model selection helper
            println("\n--- Available Production Models ---")
            GroqModel.productionModels().forEach { model ->
                println("- ${model.name}: ${model.modelId}")
            }
            
        } catch (e: GroqException) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }
    
    private suspend fun errorHandling(apiKey: String) {
        println("\n=== Error Handling ===")
        
        val client = GroqClient.create(apiKey)
        
        // Example 1: Invalid parameters
        println("\n--- Testing Invalid Parameters ---")
        try {
            // This will fail due to invalid parameters
            val response = client.createChatCompletion(
                model = GroqModel.LLAMA_3_3_70B_VERSATILE,
                messages = emptyList(), // Invalid: empty messages
                temperature = 3.0 // Invalid: temperature > 2
            )
            
            println("Response: ${response.choices.first().message.content}")
            
        } catch (e: GroqValidationException) {
            println("✅ Caught validation error as expected: ${e.message}")
        } catch (e: Exception) {
            println("❌ Unexpected error: ${e.javaClass.simpleName}: ${e.message}")
        }
        
        // Example 2: Invalid API key
        println("\n--- Testing Invalid API Key ---")
        val invalidClient = GroqClient.create("invalid-api-key")
        try {
            invalidClient.generateText(
                model = GroqModel.LLAMA_3_3_70B_VERSATILE,
                prompt = "This should fail"
            )
        } catch (e: GroqAuthenticationException) {
            println("✅ Caught authentication error as expected: ${e.message}")
        } catch (e: Exception) {
            println("❌ Unexpected error: ${e.javaClass.simpleName}: ${e.message}")
        } finally {
            invalidClient.close()
        }
        
        // Example 3: Rate limiting
        println("\n--- Testing Rate Limiting ---")
        try {
            // Send multiple requests quickly to trigger rate limiting
            repeat(10) { i ->
                val tempClient = GroqClient.create(apiKey)
                try {
                    val tempResponse = tempClient.generateText(
                        model = GroqModel.LLAMA_3_3_70B_VERSATILE,
                        prompt = "Quick test ${i + 1}",
                        maxTokens = 10
                    )
                    println("Request ${i + 1} succeeded")
                } catch (e: GroqRateLimitException) {
                    println("✅ Hit rate limit as expected. Retry after: ${e.retryAfterSeconds} seconds")
                    throw e // Re-throw to exit the loop
                } finally {
                    tempClient.close()
                }
            }
        } catch (e: GroqRateLimitException) {
            // Expected - do nothing
        } catch (e: Exception) {
            println("❌ Unexpected error: ${e.javaClass.simpleName}: ${e.message}")
        }
        
        client.close()
    }
}
