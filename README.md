# Groq Kotlin SDK

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.indiedev/groq-sdk)](https://search.maven.org/artifact/com.indiedev/groq-sdk)

A modern, type-safe Kotlin client for the [Groq API](https://console.groq.com/docs/quickstart), designed for performance and ease of use. Built with Kotlin Multiplatform for seamless integration across JVM and Android applications.

## Overview

The Groq Kotlin SDK provides a clean, idiomatic Kotlin interface to Groq's powerful inference API. It handles authentication, request formatting, response parsing, and error handling, so you can focus on building great AI-powered applications.

```kotlin
// Simple example
val client = GroqClient.create("your-api-key")
val response = client.generateText(
    model = GroqModel.LLAMA_3_3_70B_VERSATILE,
    prompt = "Explain quantum computing in simple terms"
)
```

## Table of Contents

- [Installation](#installation)
- [Getting Started](#getting-started)
- [Deep Dives](#deep-dives)
  - [Authentication](#authentication)
  - [Text Generation](#text-generation)
  - [Chat Completions](#chat-completions)
  - [Model Selection](#model-selection)
  - [Error Handling](#error-handling)
  - [Advanced Configuration](#advanced-configuration)
- [Best Practices](#best-practices)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

## Installation

### Prerequisites
- Kotlin 1.9.0 or higher
- JDK 11 or higher
- For Android: Minimum API level 21

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.indiedev:groq-sdk:1.0.0")
}
```

### Maven

```xml
<dependency>
    <groupId>com.indiedev</groupId>
    <artifactId>groq-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Getting Started

### Initialize the Client

```kotlin
import com.indiedev.groqsdk.*

// Basic initialization
val client = GroqClient.create("your-api-key")

// Or with custom configuration
val client = GroqClient.create {
    apiKey = "your-api-key"
    timeoutMillis = 30_000
    maxRetries = 3
    logLevel = LogLevel.BASIC
}
```

### Your First Request

```kotlin
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = "Explain quantum computing in simple terms"
    )
    
    println(response.choices.first().message.content)
}
```

## Deep Dives

### Authentication

#### API Keys

```kotlin
// Initialize with API key
val client = GroqClient.create("your-api-key")

// Or set it later
client.apiKey = "your-new-api-key"
```

> **Security Note**: Never hardcode API keys in source code. Use environment variables or a secure configuration management solution.

### Text Generation

#### Basic Text Generation

```kotlin
// Using the latest production model
val response = client.generateText(
    model = GroqModel.LLAMA_3_3_70B_VERSATILE,
    prompt = "Write a haiku about artificial intelligence"
)

// Using a preview model (not for production)
val previewResponse = client.generateText(
    model = GroqModel.KIMI_K2_INSTRUCT,
    prompt = "Explain quantum computing"
)
```

#### With Parameters

```kotlin
val response = client.generateText(
    model = GroqModel.LLAMA_3_3_70B_VERSATILE,
    prompt = "List three benefits of using Kotlin",
    maxTokens = 150,
    temperature = 0.7,
    topP = 0.9,
    n = 2,  // Generate 2 completions
    stop = listOf("\n"),
    presencePenalty = 0.6,
    frequencyPenalty = 0.5
)
```

### Chat Completions

#### Basic Chat

```kotlin
val messages = listOf(
    ChatMessage(role = Role.SYSTEM, content = "You are a helpful assistant."),
    ChatMessage(role = Role.USER, content = "Hello!")
)

// Using a production model
val response = client.createChatCompletion(
    model = GroqModel.LLAMA_3_3_70B_VERSATILE,
    messages = messages
)

// Using a preview model with vision capabilities
val visionMessages = listOf(
    ChatMessage(role = Role.SYSTEM, content = "You are a helpful assistant that can understand images."),
    ChatMessage(
        role = Role.USER, 
        content = "What's in this image?",
        images = listOf("https://example.com/image.jpg")
    )
)

val visionResponse = client.createChatCompletion(
    model = GroqModel.LLAMA_3_2_11B_VISION_PREVIEW,
    messages = visionMessages
)
```

#### Streaming Responses

```kotlin
client.createChatCompletionStream(
    model = GroqModel.LLAMA_3_1_70B_VERSATILE,
    messages = listOf(ChatMessage(role = Role.USER, content = "Tell me a story"))
) { chunk ->
    print(chunk.choices.first().delta.content ?: "")
    System.out.flush()
}
```

### Model Selection

#### Production Models

| Model | ID | Description | Max Tokens | Use Case |
|-------|----|-------------|------------|----------|
| Llama 3.3 70B Versatile | `LLAMA_3_3_70B_VERSATILE` | Latest general purpose model with improved performance | 8,192 | General purpose, complex tasks |
| Llama 3.1 8B Instant | `LLAMA_3_1_8B_INSTANT` | Fast, efficient model for quick responses | 8,192 | Simple queries, low-latency needs |
| Gemma 2 9B IT | `GEMMA2_9B_IT` | Lightweight, efficient model for code and chat | 8,192 | Code generation, chat applications |

#### Preview Models (Not for Production)

| Model | ID | Description | Max Tokens | Special Features |
|-------|----|-------------|------------|------------------|
| Kimi K2 Instruct | `KIMI_K2_INSTRUCT` | Moonshot AI's MoE model (1T total, 32B active) | 8,192 | Advanced reasoning, tool use |
| Qwen 3 32B | `QWEN_3_32B` | Latest generation with advanced reasoning | 8,192 | Multilingual support, complex tasks |
| Llama 3.1 70B Versatile | `LLAMA_3_1_70B_VERSATILE` | Previous generation general purpose model | 8,192 | General purpose |
| Llama 3.2 11B Vision | `LLAMA_3_2_11B_VISION_PREVIEW` | Multimodal model with vision capabilities | 8,192 | Image understanding |
| Mixtral 8x7B | `MIXTRAL_8X7B_32768` | Large context window model | 32,768 | Long documents, conversations |
| DeepSeek R1 Distill Llama 70B | `DEEPSEEK_R1_DISTILL_LLAMA_70B` | High-performance reasoning model | 8,192 | Complex reasoning tasks |
| Llama 4 Maverick 17B | `LLAMA_4_MAVERICK_17B` | Specialized for complex tasks | 8,192 | Advanced applications |
| Llama 4 Scout 17B | `LLAMA_4_SCOUT_17B` | Optimized for specific use cases | 8,192 | Specialized applications |

#### Choosing the Right Model

- **For production use**:
  - `LLAMA_3_3_70B_VERSATILE` - Best overall performance for most use cases
  - `LLAMA_3_1_8B_INSTANT` - When speed is critical
  - `GEMMA2_9B_IT` - For code generation and chat applications

- **For experimental/preview features**:
  - `KIMI_K2_INSTRUCT` - Advanced reasoning and tool use
  - `QWEN_3_32B` - Multilingual support
  - `LLAMA_3_2_11B_VISION_PREVIEW` - For image understanding
  - `MIXTRAL_8X7B_32768` - For long documents (32k tokens context)

### Error Handling

#### Basic Error Handling

```kotlin
try {
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = "Generate text"
    )
} catch (e: GroqException) {
    when (e) {
        is GroqAuthenticationException -> println("Invalid API key")
        is GroqRateLimitException -> println("Rate limit exceeded")
        is GroqApiException -> println("API error: ${e.message}")
        is GroqNetworkException -> println("Network error")
        is GroqTimeoutException -> println("Request timed out")
        else -> println("Unexpected error: ${e.message}")
    }
}
```

#### Retry Logic

```kotlin
val client = GroqClient.create {
    apiKey = "your-api-key"
    maxRetries = 3
    retryDelay = 1000L  // 1 second
}
```

### Advanced Configuration

#### Client Configuration

```kotlin
val client = GroqClient.create {
    // Required
    apiKey = "your-api-key"
    
    // Timeouts
    timeoutMillis = 30_000
    connectionTimeoutMillis = 10_000
    
    // Retry
    maxRetries = 3
    retryDelay = 1000L
    
    // Logging
    logLevel = LogLevel.BASIC  // NONE, BASIC, HEADERS, BODY
    
    // Custom HTTP client (OkHttp)
    httpClient = OkHttpClient.Builder()
        .addInterceptor(/* your interceptor */)
        .build()
}
```

#### Custom JSON Serialization

```kotlin
val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
}

val client = GroqClient.create(
    apiKey = "your-api-key",
    json = json
)
```

## Best Practices

### API Key Management

```kotlin
// Recommended: Load from environment variables
val apiKey = System.getenv("GROQ_API_KEY") ?: error("GROQ_API_KEY not set")
val client = GroqClient.create(apiKey)
```

### Error Handling

Always wrap API calls in try-catch blocks and handle potential errors appropriately:

```kotlin
suspend fun generateResponse(prompt: String): String = try {
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = prompt
    )
    response.choices.first().message.content
} catch (e: GroqException) {
    "Error generating response: ${e.message}"
}
```

### Performance Optimization

- **Reuse clients**: Create and reuse client instances
- **Batch requests**: When possible, batch multiple prompts into a single request
- **Use streaming**: For long responses, use streaming to process tokens as they arrive

## Documentation

- [Official Groq API Documentation](https://console.groq.com/docs/quickstart)
- [API Reference](https://github.com/shiv207/groq-kotlin-sdk/wiki/API-Reference)
- [Examples](https://github.com/shiv207/groq-kotlin-sdk/tree/main/examples)

## Contributing

Contributions are welcome! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

- For bugs and feature requests, please [open an issue](https://github.com/shiv207/groq-kotlin-sdk/issues)
- For direct support, email shivamshsr8@gmail.com

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### v1.0.0
- Initial release
- Support for chat completions
- Kotlin Multiplatform support
- Retry logic with exponential backoff
