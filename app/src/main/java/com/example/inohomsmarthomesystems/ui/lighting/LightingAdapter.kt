package com.example.inohomsmarthomesystems.ui.lighting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inohomsmarthomesystems.R

class LightingAdapter(
    private var items: List<LightingControl>,
    private val onItemClicked: (LightingControl) -> Unit
) : RecyclerView.Adapter<LightingAdapter.LightingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lighting_control, parent, false)
        return LightingViewHolder(view)
    }

    override fun onBindViewHolder(holder: LightingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<LightingControl>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class LightingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgBulb: ImageView = itemView.findViewById(R.id.imgBulb)
        private val tvLightName: TextView = itemView.findViewById(R.id.tvLightName)

        fun bind(item: LightingControl) {
            tvLightName.text = item.name
            // Lambanın açık/kapalı olduğunu currentValue ile ayarlıyoruz
            imgBulb.setImageResource(
                if (item.currentValue == 1)
                    R.drawable.ic_light_on
                else
                    R.drawable.ic_lighting
            )
            itemView.setOnClickListener { onItemClicked(item) }
        }
    }
}
