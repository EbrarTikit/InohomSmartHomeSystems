package com.example.inohomsmarthomesystems.data.model

import com.google.gson.annotations.SerializedName

data class AuthenticationResponse(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("params")
    val params: List<String>,
    
    @SerializedName("method")
    val method: String,
    
    @SerializedName("error")
    val error: String?,
    
    @SerializedName("is_request")
    val isRequest: Boolean
) 