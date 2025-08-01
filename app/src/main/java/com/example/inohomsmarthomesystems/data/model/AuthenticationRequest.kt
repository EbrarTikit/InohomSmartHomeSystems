package com.example.inohomsmarthomesystems.data.model

import com.google.gson.annotations.SerializedName

data class AuthenticationRequest(
    @SerializedName("is_request")
    val isRequest: Boolean = true,
    
    @SerializedName("id")
    val id: Int = 8,
    
    @SerializedName("params")
    val params: List<LoginParams>,
    
    @SerializedName("method")
    val method: String = "Authenticate"
)
data class LoginParams(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("password")
    val password: String
) 