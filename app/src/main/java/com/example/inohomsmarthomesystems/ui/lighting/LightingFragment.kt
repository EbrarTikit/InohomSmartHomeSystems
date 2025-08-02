package com.example.inohomsmarthomesystems.ui.lighting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.inohomsmarthomesystems.R
import com.example.inohomsmarthomesystems.data.remote.WebSocketService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class LightingFragment : Fragment() {

    @Inject
    lateinit var webSocketService: WebSocketService
    private lateinit var lightingAdapter: LightingAdapter
    private val lightingControls = mutableListOf<LightingControl>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_lighting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewLights)
        lightingAdapter = LightingAdapter(lightingControls) { clickedItem ->
            // Bu fonksiyon, bir ampule tıklandığında websocket üzerinden aç/kapa komutu gönderir.
            val msg =
                """{"is_request":true,"id":84,"params":[{"id":"${clickedItem.id}","value":${if (clickedItem.currentValue == 1) 0 else 1}}],"method":"UpdateControlValue"}"""
            webSocketService.sendRawMessage(msg)
        }
        recyclerView.adapter = lightingAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // GetControlList mesajını gönder
        webSocketService.sendRawMessage(
            """{"is_request":true,"id":5,"params":[{}],"method":"GetControlList"}"""
        )
        
        // Bu fonksiyon, websocket üzerinden gelen mesajları dinler ve gerekli işlemleri gerçekleştirir.
        lifecycleScope.launch {
            webSocketService.authenticationResponse.collectLatest { resp ->
                if (resp?.method == "GetControlList" && resp.params != null) {
                    // Aydınlatma listesi parse
                    parseAndUpdateLightList(resp)
                } else if (resp?.method == "OnEntityUpdated" && resp.params != null) {
                    // Güncelleme mesajı parse
                    updateLightStatus(resp)
                }
            }
        }
    }

    // Bu fonksiyon, backend'den gelen aydınlatma listesini parse eder ve ekranda günceller.
    private fun parseAndUpdateLightList(resp: Any) {
        try {
            val dataList = mutableListOf<LightingControl>()
            val respJson = JSONObject(resp.toString())
            val paramsArray = respJson.optJSONArray("params")
            if (paramsArray != null && paramsArray.length() > 0) {
                val obj = paramsArray.getJSONObject(0)
                val dataArr = obj.optJSONArray("data") ?: return
                for (i in 0 until dataArr.length()) {
                    val dev = dataArr.getJSONObject(i)
                    val id = dev.optString("id")
                    val name = dev.optString("name")
                    val isActive = dev.optBoolean("is_active", false)
                    val currentValue = dev.optInt("current_value", 0)
                    dataList.add(LightingControl(id, name, isActive, currentValue))
                }

                lightingControls.clear()
                lightingControls.addAll(dataList)
                lightingAdapter.submitList(lightingControls.toList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Bu fonksiyon, backend'den gelen güncellemesini alır ve UI'da günceller.
    private fun updateLightStatus(resp: Any) {
        try {
            val respJson = JSONObject(resp.toString())
            val paramsArray = respJson.optJSONArray("params")
            if (paramsArray != null && paramsArray.length() > 0) {
                val obj = paramsArray.getJSONObject(0)
                val entity = obj.optJSONObject("entity") ?: return
                val id = entity.optString("id")
                val currentValue = entity.optInt("current_value", 0)
                val idx = lightingControls.indexOfFirst { it.id == id }
                if (idx >= 0) {
                    val light = lightingControls[idx]
                    lightingControls[idx] = light.copy(currentValue = currentValue)
                    lightingAdapter.submitList(lightingControls.toList())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}