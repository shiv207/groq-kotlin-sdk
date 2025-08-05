# Groq Kotlin SDK

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.indiedev/groq-sdk)](https://search.maven.org/artifact/com.indiedev/groq-sdk)

A minimal, type-safe Kotlin client for the Groq API.

```kotlin
// Simple example
val client = GroqClient.create("your-api-key")
val response = client.generateText(
    model = GroqModel.LLAMA_3_1_70B_VERSATILE,
    prompt = "Explain quantum computing in simple terms"
)
```

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("com.indiedev:groq-sdk:1.0.0")
```

### Maven

```xml
<dependency>
    <groupId>com.indiedev</groupId>
    <artifactId>groq-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

```kotlin
import com.indiedev.groqsdk.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = GroqClient.create("your-api-key")
    
    // Text generation
    val response = client.generateText(
        model = GroqModel.LLAMA_3_1_70B_VERSATILE,
        prompt = "Explain quantum computing in simple terms"
    )
    println(response.choices.first().message.content)
    
    // Chat completion
    val chatResponse = client.createChatCompletion(
        model = GroqModel.MIXTRAL_8X7B_32768,
        messages = listOf(
            ChatMessage(role = Role.SYSTEM, content = "You are helpful"),
            ChatMessage(role = Role.USER, content = "Hello!")
        )
    )
    println(chatResponse.choices.first().message.content)
}
```

## Documentation

- [API Reference](https://docs.groq.com/)
- [GitHub Issues](https://github.com/shiv207/groq-kotlin-sdk/issues)
- Contact: shivamshsr8@gmail.com

## License

MIT

### v1.0.0
- Initial release
- Support for chat completions
- Kotlin Multiplatform support
- Retry logic with exponential backoff
