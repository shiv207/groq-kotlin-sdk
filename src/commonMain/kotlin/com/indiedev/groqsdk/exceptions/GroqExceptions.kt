package com.indiedev.groqsdk.exceptions

/**
 * Base exception for all Groq SDK errors
 */
abstract class GroqException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown when API key is invalid or missing
 */
class GroqAuthenticationException(
    message: String = "Invalid or missing API key",
    cause: Throwable? = null
) : GroqException(message, cause)

/**
 * Exception thrown when API request fails
 */
class GroqApiException(
    val statusCode: Int,
    message: String,
    val errorCode: String? = null,
    cause: Throwable? = null
) : GroqException("API Error [$statusCode]: $message", cause)

/**
 * Exception thrown when network request fails
 */
class GroqNetworkException(
    message: String = "Network error occurred",
    cause: Throwable? = null
) : GroqException(message, cause)

/**
 * Exception thrown when response parsing fails
 */
class GroqParsingException(
    message: String = "Failed to parse API response",
    cause: Throwable? = null
) : GroqException(message, cause)

/**
 * Exception thrown when request validation fails
 */
class GroqValidationException(
    message: String,
    cause: Throwable? = null
) : GroqException(message, cause)

/**
 * Exception thrown when rate limit is exceeded
 */
class GroqRateLimitException(
    message: String = "Rate limit exceeded",
    val retryAfterSeconds: Long? = null,
    cause: Throwable? = null
) : GroqException(message, cause)

/**
 * Exception thrown when request times out
 */
class GroqTimeoutException(
    message: String = "Request timed out",
    cause: Throwable? = null
) : GroqException(message, cause)
