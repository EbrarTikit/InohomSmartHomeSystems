package com.example.inohomsmarthomesystems.data.model

import com.google.gson.annotations.SerializedName

data class ControlListResponse(
    val id: Int,
    val params: List<ParamData>,
    val method: String,
    val error: Any?,
    @SerializedName("is_request")
    val isRequest: Boolean
)

data class ParamData(
    val data: List<ControlData>
)

data class ControlData(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type_id")
    val typeId: String,
    @SerializedName("bridge_device_id")
    val bridgeDeviceId: String,
    @SerializedName("current_value")
    val currentValue: Int,
    @SerializedName("slot")
    val slot: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("temperature_settings")
    val temperatureSettings: TemperatureSettings? = null,
    @SerializedName("area_id")
    val areaId: String,
    @SerializedName("parameters")
    val parameters: Parameters
)

data class TemperatureSettings(
    @SerializedName("has_heating")
    val hasHeating: Boolean,
    @SerializedName("has_cooling")
    val hasCooling: Boolean,
    @SerializedName("bridge_device_id")
    val bridgeDeviceId: String,
    @SerializedName("virtual_control_id")
    val virtualControlId: String,
    @SerializedName("input_id")
    val inputId: String,
    @SerializedName("is_mode_heating")
    val isModeHeating: Boolean,
    val whole: Int,
    val fraction: Int
)

data class Parameters(
    @SerializedName("default_value")
    val defaultValue: Int,
    @SerializedName("output_number")
    val outputNumber: Int,
    @SerializedName("should_output_reverse")
    val shouldOutputReverse: Boolean,
    @SerializedName("should_remember_last_value")
    val shouldRememberLastValue: Boolean,
    @SerializedName("end_time")
    val endTime: String? = null,
    @SerializedName("is_notification")
    val isNotification: Boolean? = null,
    @SerializedName("start_time")
    val startTime: String? = null
)

