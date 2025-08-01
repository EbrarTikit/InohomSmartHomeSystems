package com.example.inohomsmarthomesystems.data.remote

import android.util.Log
import com.example.inohomsmarthomesystems.data.model.AuthenticationRequest
import com.example.inohomsmarthomesystems.data.model.AuthenticationResponse
import com.example.inohomsmarthomesystems.data.model.LoginParams
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.framing.Framedata
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton
import com.example.inohomsmarthomesystems.utils.Constants
import com.example.inohomsmarthomesystems.utils.Constants.SERVER_IP
import com.example.inohomsmarthomesystems.utils.Constants.SERVER_PORT
import com.example.inohomsmarthomesystems.utils.Constants.SERVER_URL
import com.example.inohomsmarthomesystems.utils.Constants.TAG
import com.example.inohomsmarthomesystems.utils.state.ConnectionState

@Singleton
class WebSocketService @Inject constructor() {
    private var webSocketClient: WebSocketClient? = null
    private val gson = Gson()
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _authenticationResponse = MutableStateFlow<AuthenticationResponse?>(null)
    val authenticationResponse: StateFlow<AuthenticationResponse?> = _authenticationResponse
    

    //WebSocket bağlantısını başlatıyoruz
    fun connect() {
        try {
            Log.d(TAG, "WebSocket bağlantısı başlatılıyor: $SERVER_URL")

            testNetworkConnection()
            
            webSocketClient = object : WebSocketClient(URI(SERVER_URL)) {
                
                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d(TAG, "WebSocket bağlantısı açıldı - Status: ${handshakedata?.httpStatus}, StatusMessage: ${handshakedata?.httpStatusMessage}")
                    _connectionState.value = ConnectionState.CONNECTED
                }
                
                override fun onMessage(message: String?) {
                    Log.d(TAG, "WebSocket mesajı alındı: $message")
                    message?.let { handleMessage(it) }
                }
                
                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d(TAG, "WebSocket bağlantısı kapandı: code=$code, reason=$reason, remote=$remote")
                    
                    when (code) {
                        -1 -> Log.e(TAG, "WebSocket bağlantısı başarısız - Server erişilemez veya network sorunu")
                        1000 -> Log.d(TAG, "WebSocket normal kapatma")
                        1001 -> Log.d(TAG, "WebSocket going away")
                        1002 -> Log.e(TAG, "WebSocket protocol error")
                        1003 -> Log.e(TAG, "WebSocket unsupported data")
                        1006 -> Log.e(TAG, "WebSocket abnormal closure - Bağlantı aniden kesildi")
                        else -> Log.e(TAG, "WebSocket unknown close code: $code")
                    }
                    
                    _connectionState.value = ConnectionState.DISCONNECTED
                }
                
                override fun onError(ex: Exception?) {
                    Log.e(TAG, "WebSocket hatası: ${ex?.message}")
                    Log.e(TAG, "WebSocket error details: ${ex?.stackTraceToString()}")
                    _connectionState.value = ConnectionState.ERROR
                }
            }

            webSocketClient?.connectionLostTimeout = 10
            
            // Bağlantıyı başlatır
            Log.d(TAG, "WebSocket connect() çağrılıyor...")
            webSocketClient?.connect()
            
        } catch (e: Exception) {
            Log.e(TAG, "WebSocket bağlantı hatası: ${e.message}")
            Log.e(TAG, "WebSocket connection error details: ${e.stackTraceToString()}")
            _connectionState.value = ConnectionState.ERROR
        }
    }

    fun disconnect() {
        Log.d(TAG, "WebSocket bağlantısı kapatılıyor")
        webSocketClient?.close()
        webSocketClient = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    //Authentication request gönderiyoruz
    fun sendAuthenticationRequest(username: String, password: String) {
        try {
            val request = AuthenticationRequest(
                params = listOf(
                    LoginParams(username, password)
                )
            )
            
            val jsonRequest = gson.toJson(request)
            Log.d(TAG, "Authentication request gönderiliyor: $jsonRequest")
            
            webSocketClient?.send(jsonRequest)
            
        } catch (e: Exception) {
            Log.e(TAG, "Authentication request gönderme hatası: ${e.message}")
        }
    }
    

    // Gelen json mesajı
    private fun handleMessage(message: String) {
        try {
            Log.d(TAG, "Mesaj işleniyor: $message")
            
            // Echo server test için özel kontrol
            if (SERVER_URL.contains("echo.websocket.org")) {
                handleEchoServerMessage(message)
                return
            }
            
            val response = gson.fromJson(message, AuthenticationResponse::class.java)
            
            when (response.method) {
                "OnAuthenticated" -> {
                    Log.d(TAG, "Authentication başarılı: ${response.params}")
                    _authenticationResponse.value = response
                }
                else -> {
                    Log.d(TAG, "Bilinmeyen method: ${response.method}")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Mesaj işleme hatası: ${e.message}")
        }
    }
    
  // Echo server mesajlarını işler
    private fun handleEchoServerMessage(message: String) {
        try {
            Log.d(TAG, "Echo server mesajı alındı, authentication simüle ediliyor")
            
            // Mock authentication response oluştur
            val mockResponse = AuthenticationResponse(
                id = 792,
                params = listOf("demo"),
                method = "OnAuthenticated",
                error = null,
                isRequest = true
            )
            
            Log.d(TAG, "Mock authentication response: ${gson.toJson(mockResponse)}")
            _authenticationResponse.value = mockResponse
            
        } catch (e: Exception) {
            Log.e(TAG, "Echo server mesaj işleme hatası: ${e.message}")
        }
    }
    
    /**
     * Network bağlantısını test eder
     */
    private fun testNetworkConnection() {
        try {
            Log.d(TAG, "Network bağlantısı test ediliyor...")
            val socket = java.net.Socket()
            socket.connect(java.net.InetSocketAddress(SERVER_IP, SERVER_PORT), 5000)
            Log.d(TAG, "Network bağlantısı başarılı - Server erişilebilir")
            socket.close()
            
        } catch (e: Exception) {
            Log.e(TAG, "Network bağlantısı başarısız: ${e.message}")
            Log.e(TAG, "Server $SERVER_IP:$SERVER_PORT erişilemez")
        }
    }
} 