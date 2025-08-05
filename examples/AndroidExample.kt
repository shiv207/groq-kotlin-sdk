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
    
    private val groqClient = GroqClient.create {
        apiKey(apiKey)
        enableLogging(BuildConfig.DEBUG)
        retryAttempts(3)
        timeout(30_000L)
    }
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun sendMessage(content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // Add user message to the conversation
            val userMessage = ChatMessage(MessageRole.USER, content)
            val currentMessages = _messages.value + userMessage
            _messages.value = currentMessages
            
            try {
                val response = groqClient.createChatCompletion(
                    model = GroqModel.LLAMA_3_1_70B_VERSATILE,
                    messages = currentMessages,
                    temperature = 0.7,
                    maxTokens = 1000
                )
                
                // Add assistant response to the conversation
                val assistantMessage = response.choices.first().message
                _messages.value = currentMessages + assistantMessage
                
            } catch (e: GroqAuthenticationException) {
                _error.value = "Invalid API key. Please check your configuration."
            } catch (e: GroqRateLimitException) {
                _error.value = "Rate limit exceeded. Please try again in ${e.retryAfterSeconds ?: 60} seconds."
            } catch (e: GroqApiException) {
                _error.value = "API Error: ${e.message}"
            } catch (e: GroqNetworkException) {
                _error.value = "Network error. Please check your connection."
            } catch (e: GroqTimeoutException) {
                _error.value = "Request timed out. Please try again."
            } catch (e: GroqException) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearMessages() {
        _messages.value = emptyList()
        _error.value = null
    }
    
    fun clearError() {
        _error.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        groqClient.close()
    }
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
