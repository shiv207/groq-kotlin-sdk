<div align="center">
  <h1>Groq Kotlin SDK</h1>
  <p>Elegant, type-safe access to Groq's lightning-fast inference API</p>
  
  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
  [![Maven Central](https://img.shields.io/maven-central/v/com.indiedev/groq-sdk)](https://search.maven.org/artifact/com.indiedev/groq-sdk)
  [![API Docs](https://img.shields.io/badge/API-Docs-00B3FF.svg)](https://shiv207.github.io/groq-kotlin-sdk/)

  <img src="https://groq.com/wp-content/uploads/2023/10/logo-groq-dark.svg" alt="Groq Logo" width="200"/>
</div>

## Overview

**Groq Kotlin SDK** is an unofficial, community-maintained Kotlin Multiplatform library that provides elegant, type-safe access to [Groq's](https://groq.com/) high-performance inference API. This SDK is not affiliated with or endorsed by Groq Inc.

```kotlin
// Simple example
val client = GroqClient.create("your-api-key")
val response = client.generateText(
    model = GroqModel.LLAMA_3_1_70B_VERSATILE,
    prompt = "Explain quantum computing in simple terms"
)
```

## Why Groq Kotlin SDK?

### 🚀 Blazing Fast Performance
Harness the power of Groq's LPU (Language Processing Unit) technology for sub-millisecond latency, consistently maintained across traffic loads.

### 🧩 First-Class Kotlin Support
Built with Kotlin Multiplatform, coroutines, and serialization for a seamless development experience across Android, JVM, and other Kotlin targets.

### 🔐 Enterprise-Grade Reliability
Comprehensive error handling, automatic retries with exponential backoff, and detailed usage metrics built-in.

## Features

- **Full Model Support**: Access all Groq models including Llama 3.1, Mixtral, and Gemma
- **Type-Safe API**: Compile-time safety with Kotlin's type system
- **Asynchronous by Default**: Built with Kotlin Coroutines for non-blocking operations
- **Automatic Retry**: Configurable retry logic with exponential backoff
- **Comprehensive Error Handling**: Detailed exception hierarchy for all failure cases
- **Token Usage Tracking**: Monitor usage with detailed token counts
- **Streaming Support**: Real-time token streaming for chat completions

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.indiedev:groq-sdk:1.0.0")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.indiedev:groq-sdk:1.0.0'
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

## Quick Start

### Prerequisites
- A Groq API key from [Groq Console](https://console.groq.com/)
- Kotlin 1.9.0 or higher
- JDK 11 or higher

### Basic Usage

```kotlin
import com.indiedev.groqsdk.*
import com.indiedev.groqsdk.config.*
import com.indiedev.groqsdk.models.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Initialize client with your API key
    val client = GroqClient.create("your-api-key-here")
    
    // Generate text with a single request
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = "Explain quantum computing in simple terms"
    )
    
    // Print the generated text
    println(response.choices.first().message.content)
}
```

### Chat Completion Example

```kotlin
val chatResponse = client.createChatCompletion(
    model = GroqModel.MIXTRAL_8X7B_32768,
    messages = listOf(
        ChatMessage(role = Role.SYSTEM, content = "You are a helpful assistant."),
        ChatMessage(role = Role.USER, content = "What's the weather like today?")
    ),
    temperature = 0.7,
    maxTokens = 500
)

println(chatResponse.choices.first().message.content)
```

## Advanced Usage

### Streaming Responses

Process responses as they're generated for real-time applications:

```kotlin
val request = ChatCompletionRequest(
    model = GroqModel.LLAMA_3_1_70B_VERSATILE,
    messages = listOf(
        ChatMessage(role = Role.USER, content = "Write a short poem about Kotlin")
    ),
    stream = true
)

client.createChatCompletionStream(request) { chunk ->
    print(chunk.choices.first().delta.content ?: "")
    System.out.flush()
}
```
```

### Available Models

Groq Kotlin SDK supports all major models available through the Groq API:

```kotlin
// Llama 3.1 Series
GroqModel.LLAMA_3_1_70B_VERSATILE  // 70B parameter model, best overall performance
GroqModel.LLAMA_3_1_8B_VERSATILE   // 8B parameter model, faster response times

// Mixtral
GroqModel.MIXTRAL_8X7B_32768       // Mixtral 8x7B with 32k context

// Gemma
GroqModel.GEMMA_7B_IT              // 7B parameter Gemma model
```

### Error Handling

The SDK provides a comprehensive exception hierarchy:

```kotlin
try {
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = "Generate some text"
    )
} catch (e: GroqApiException) {
    // Handle API errors (4xx/5xx responses)
    println("API Error: ${e.message}")
} catch (e: GroqAuthenticationException) {
    // Handle authentication issues
    println("Authentication failed. Please check your API key.")
} catch (e: GroqRateLimitException) {
    // Handle rate limiting
    println("Rate limit exceeded. Please try again later.")
    println("Reset time: ${e.resetTime}")
} catch (e: GroqException) {
    // Handle other Groq-related errors
    println("Error: ${e.message}")
}
```

### Custom Configuration

Configure the client with custom settings:

```kotlin
val client = GroqClient.create(
    config = GroqConfig(
        apiKey = "your-api-key",
        baseUrl = "https://api.groq.com/openai/v1", // Custom endpoint if needed
        timeoutSeconds = 30L,
        maxRetries = 3,
        logLevel = HttpLogLevel.HEADERS
    )
)
```

## Advanced Configuration

Customize the client with additional options:

```kotlin
val client = GroqClient.create {
    apiKey("your-api-key")
    enableLogging(true)  // Enable detailed HTTP logging
    timeout(60_000L)     // 60 second timeout
    retryAttempts(5)     // Number of retry attempts
    baseUrl("https://api.groq.com/openai/v1")  // Custom endpoint if needed
}
```

## Available Models

Groq Kotlin SDK supports the following models:

| Model | ID | Best For |
|-------|----|---------| 
| Llama 3.1 405B Reasoning | `LLAMA_3_1_405B_REASONING` | Complex reasoning and analysis |
| Llama 3.1 70B Versatile | `LLAMA_3_1_70B_VERSATILE` | Balanced performance for most use cases |
| Llama 3.1 8B Instant | `LLAMA_3_1_8B_INSTANT` | Fast responses for simple queries |
| Llama 3.2 11B Vision | `LLAMA_3_2_11B_VISION_PREVIEW` | Image understanding and analysis |
| Mixtral 8x7B | `MIXTRAL_8X7B_32768` | Long-context applications (32K tokens) |
| Gemma 7B IT | `GEMMA_7B_IT` | Instruction following and code generation |
| Gemma2 9B IT | `GEMMA2_9B_IT` | Enhanced instruction following |

## Error Handling

The SDK provides detailed error handling with specific exception types:

```kotlin
try {
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = "Hello world"
    )
    println(response)
} catch (e: GroqAuthenticationException) {
    println("❌ Invalid API key")
} catch (e: GroqRateLimitException) {
    println("⏳ Rate limit exceeded. Retry after: ${e.retryAfterSeconds}s")
} catch (e: GroqApiException) {
    println("⚠️ API error (${e.statusCode}): ${e.message}")
} catch (e: GroqNetworkException) {
    println("🔌 Network error: ${e.message}")
} catch (e: GroqTimeoutException) {
    println("⏱️ Request timed out")
} catch (e: GroqValidationException) {
    println("❌ Invalid request: ${e.message}")
} catch (e: Exception) {
    println("❌ Unexpected error: ${e.message}")
}
```

## Android Integration

### Permissions

Add internet permission to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### ViewModel Example

```kotlin
class ChatViewModel : ViewModel() {
    private val groqClient = GroqClient.create("your-api-key")
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Add user message
                _messages.update { it + ChatMessage(role = Role.USER, content = message) }
                
                // Get AI response
                val response = groqClient.createChatCompletion(
                    model = GroqModel.LLAMA_3_1_8B_INSTANT,
                    messages = _messages.value,
                    temperature = 0.7f
                )
                
                // Add AI response
                val aiMessage = response.choices.first().message
                _messages.update { it + aiMessage }
                
            } catch (e: Exception) {
                // Handle error
                _messages.update { it + ChatMessage(
                    role = Role.SYSTEM,
                    content = "Error: ${e.message}"
                )}
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        groqClient.close()
    }
}
```

## Roadmap

- [x] Core chat completion API
- [x] Support for all Groq models
- [x] Comprehensive error handling
- [x] Android integration
- [ ] Streaming responses
- [ ] File upload support
- [ ] More detailed documentation
- [ ] Additional utility functions

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Building Locally

```bash
# Clone the repository
git clone https://github.com/shiv207/groq-kotlin-sdk.git
cd groq-kotlin-sdk

# Build the project
./gradlew build

# Run tests
./gradlew test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Disclaimer

This is an unofficial SDK and is not affiliated with or endorsed by Groq Inc. All product and company names are trademarks™ or registered® trademarks of their respective holders. Use of them does not imply any affiliation with or endorsement by them.

## Support

For support, please [open an issue](https://github.com/shiv207/groq-kotlin-sdk/issues) on GitHub.

## Acknowledgments

- Groq for their amazing inference technology
- Kotlin team for the fantastic language and ecosystem
- All contributors who help improve this SDK

### v1.0.0
- Initial release
- Support for chat completions
- Kotlin Multiplatform support
- Comprehensive error handling
- Retry logic with exponential backoff
