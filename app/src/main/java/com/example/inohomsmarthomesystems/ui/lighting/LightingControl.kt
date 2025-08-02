package com.example.inohomsmarthomesystems.ui.lighting

data class LightingControl(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val currentValue: Int
)