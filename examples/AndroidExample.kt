package com.indiedev.groqsdk.examples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indiedev.groqsdk.*
import com.indiedev.groqsdk.config.*
import com.indiedev.groqsdk.models.*
import com.indiedev.groqsdk.exceptions.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Example Android ViewModel using the Groq SDK
 */
class ChatViewModel(apiKey: String) : ViewModel() {
    
    // Available models for selection
    val availableModels = listOf(
        ModelInfo("Llama 3.3 70B (Recommended)", GroqModel.LLAMA_3_3_70B_VERSATILE, isPreview = false),
        ModelInfo("Llama 3.1 8B (Fast)", GroqModel.LLAMA_3_1_8B_INSTANT, isPreview = false),
        ModelInfo("Gemma 2 9B (Code)", GroqModel.GEMMA2_9B_IT, isPreview = false),
        ModelInfo("Kimi K2 (Preview)", GroqModel.KIMI_K2_INSTRUCT, isPreview = true),
        ModelInfo("Qwen 3 32B (Preview)", GroqModel.QWEN_3_32B, isPreview = true)
    )
    
    private var selectedModel: GroqModel = GroqModel.LLAMA_3_3_70B_VERSATILE
    
    private val groqClient = GroqClient.create {
        apiKey(apiKey)
        enableLogging(BuildConfig.DEBUG)
        retryAttempts(3)
        timeout(45_000L) // Longer timeout for mobile
    }
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    // Update selected model
    fun selectModel(model: GroqModel) {
        selectedModel = model
        _uiState.update { it.copy(selectedModelName = availableModels.find { m -> m.model == model }?.displayName ?: "") }
    }
    
    fun sendMessage(content: String) {
        if (content.isBlank() || _uiState.value.isLoading) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Add user message to the conversation
            val userMessage = ChatMessage(MessageRole.USER, content)
            val currentMessages = _uiState.value.messages + userMessage
            _uiState.update { it.copy(messages = currentMessages) }
            
            try {
                // Show typing indicator
                val typingIndicator = ChatMessage(MessageRole.ASSISTANT, "...")
                _uiState.update { it.copy(messages = currentMessages + typingIndicator) }
                
                // Stream the response
                groqClient.createChatCompletion(
                    model = selectedModel,
                    messages = currentMessages,
                    temperature = 0.7,
                    maxTokens = 1000,
                    stream = true
                ) { chunk ->
                    val content = chunk.choices.firstOrNull()?.delta?.content ?: return@createChatCompletion
                    
                    // Update the last message with the new content
                    _uiState.update { state ->
                        val messages = state.messages.toMutableList()
                        if (messages.isNotEmpty() && messages.last().role == MessageRole.ASSISTANT) {
                            messages[messages.lastIndex] = messages.last().copy(
                                content = (messages.last().content + content).trimStart()
                            )
                        } else {
                            messages.add(ChatMessage(MessageRole.ASSISTANT, content))
                        }
                        state.copy(messages = messages)
                    }
                }
                
            } catch (e: GroqAuthenticationException) {
                _uiState.update { it.copy(error = "Invalid API key. Please check your configuration.") }
            } catch (e: GroqRateLimitException) {
                _uiState.update { it.copy(error = "Rate limit exceeded. Please try again in ${e.retryAfterSeconds ?: 60} seconds.") }
            } catch (e: GroqApiException) {
                _uiState.update { it.copy(error = "API Error: ${e.message}") }
            } catch (e: GroqNetworkException) {
                _uiState.update { it.copy(error = "Network error. Please check your connection.") }
            } catch (e: GroqTimeoutException) {
                _uiState.update { it.copy(error = "Request timed out. Please try again.") }
            } catch (e: GroqException) {
                _uiState.update { it.copy(error = "Error: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
                
                // Remove typing indicator if it exists
                _uiState.update { state ->
                    val messages = state.messages.filterNot { it.content == "..." }
                    state.copy(messages = messages)
                }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { 
            it.copy(
                messages = emptyList(),
                error = null
            )
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        groqClient.close()
    }
}

// UI state for the chat screen
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedModelName: String = ""
)

// Wrapper for model information
data class ModelInfo(
    val displayName: String,
    val model: GroqModel,
    val isPreview: Boolean
)
}

/**
 * Example Jetpack Compose UI usage
 */
/*
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
            
            if (isLoading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        error?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        MessageInput(
            onSendMessage = viewModel::sendMessage,
            enabled = !isLoading
        )
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
*/
