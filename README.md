# Groq SDK for Kotlin

A modern, type-safe Kotlin Multiplatform SDK for the Groq API, supporting Android, JVM, and other Kotlin targets.

## Features

- 🚀 **Kotlin Multiplatform** - Works on Android, JVM, and more
- 🔒 **Type Safe** - Full Kotlin type safety with data classes
- ⚡ **Coroutines** - Built with Kotlin Coroutines for async operations
- 🔄 **Retry Logic** - Automatic retry with exponential backoff
- 📝 **Comprehensive Error Handling** - Detailed exception types
- 🎯 **Multiple Models** - Support for all Groq models
- 🛠 **Easy Configuration** - Builder pattern for configuration
- 📊 **Usage Tracking** - Token usage information in responses

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.indiedev:groq-sdk:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.indiedev:groq-sdk:1.0.0'
}
```

## Quick Start

### Basic Usage

```kotlin
import com.indiedev.groqsdk.*
import com.indiedev.groqsdk.config.*
import com.indiedev.groqsdk.models.*

// Create a client with your API key
val client = GroqClient.create("your-api-key-here")

// Generate text
val response = client.generateText(
    model = GroqModel.LLAMA_3_1_70B_VERSATILE,
    prompt = "Explain quantum computing in simple terms"
)

println(response)
```

### Advanced Configuration

```kotlin
val client = GroqClient.create {
    apiKey("your-api-key")
    enableLogging(true)
    timeout(60_000L) // 60 seconds
    retryAttempts(5)
    baseUrl("https://api.groq.com/openai/v1") // Custom base URL if needed
}
```

### Chat Conversations

```kotlin
val messages = listOf(
    ChatMessage(MessageRole.SYSTEM, "You are a helpful AI assistant."),
    ChatMessage(MessageRole.USER, "What's the capital of France?"),
    ChatMessage(MessageRole.ASSISTANT, "The capital of France is Paris."),
    ChatMessage(MessageRole.USER, "What's the population?")
)

val response = client.createChatCompletion(
    model = GroqModel.LLAMA_3_1_70B_VERSATILE,
    messages = messages,
    temperature = 0.7,
    maxTokens = 150
)

println(response.choices.first().message.content)
println("Tokens used: ${response.usage.totalTokens}")
```

## Available Models

| Model | ID | Best For |
|-------|----|---------| 
| Llama 3.1 405B Reasoning | `LLAMA_3_1_405B_REASONING` | Complex reasoning tasks |
| Llama 3.1 70B Versatile | `LLAMA_3_1_70B_VERSATILE` | General purpose, balanced speed/quality |
| Llama 3.1 8B Instant | `LLAMA_3_1_8B_INSTANT` | Fast responses, simple tasks |
| Llama 3.2 11B Vision | `LLAMA_3_2_11B_VISION_PREVIEW` | Vision tasks (preview) |
| Mixtral 8x7B | `MIXTRAL_8X7B_32768` | Long context tasks |
| Gemma 7B IT | `GEMMA_7B_IT` | Instruction following |
| Gemma2 9B IT | `GEMMA2_9B_IT` | Enhanced instruction following |

## Error Handling

The SDK provides comprehensive error handling with specific exception types:

```kotlin
try {
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = "Hello world"
    )
    println(response)
} catch (e: GroqAuthenticationException) {
    println("Invalid API key: ${e.message}")
} catch (e: GroqRateLimitException) {
    println("Rate limit exceeded. Retry after: ${e.retryAfterSeconds} seconds")
} catch (e: GroqApiException) {
    println("API error ${e.statusCode}: ${e.message}")
} catch (e: GroqNetworkException) {
    println("Network error: ${e.message}")
} catch (e: GroqTimeoutException) {
    println("Request timed out: ${e.message}")
} catch (e: GroqValidationException) {
    println("Invalid request: ${e.message}")
}
```

## Configuration Options

| Option | Default | Description |
|--------|---------|-------------|
| `apiKey` | *Required* | Your Groq API key |
| `baseUrl` | `https://api.groq.com/openai/v1` | API base URL |
| `timeoutMillis` | `30000` | Request timeout in milliseconds |
| `enableLogging` | `false` | Enable HTTP request/response logging |
| `retryAttempts` | `3` | Number of retry attempts for failed requests |

## Android Integration

### Permissions

Add internet permission to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Usage in Android

```kotlin
class ChatViewModel : ViewModel() {
    private val groqClient = GroqClient.create("your-api-key")
    
    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                val response = groqClient.generateText(
                    model = GroqModel.LLAMA_3_1_8B_INSTANT,
                    prompt = message
                )
                // Update UI with response
            } catch (e: GroqException) {
                // Handle error
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        groqClient.close()
    }
}
```

## Streaming (Coming Soon)

Streaming support is planned for future releases:

```kotlin
// Future API
client.createChatCompletionStream(request) { chunk ->
    println(chunk.choices.first().delta.content)
}
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 📧 Email: support@indiedev.com
- 🐛 Issues: [GitHub Issues](https://github.com/indiedev/groq-sdk/issues)
- 📖 Documentation: [API Docs](https://docs.indiedev.com/groq-sdk)

## Changelog

### v1.0.0
- Initial release
- Support for chat completions
- Kotlin Multiplatform support
- Comprehensive error handling
- Retry logic with exponential backoff
