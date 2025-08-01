package com.example.inohomsmarthomesystems.data.model

import com.google.gson.annotations.SerializedName

data class ControlListRequest(
    val id: Int,
    @SerializedName("is_request")
    val isRequest: Boolean,
    val method: String,
    val params: List<Param>
)