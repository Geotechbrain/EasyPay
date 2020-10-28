package com.geodeveloper.easypay.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.activity.BuyAirtimeActivity
import com.geodeveloper.easypay.activity.ServiceVariationActivity
import com.geodeveloper.easypay.models.airtime.Airtime

class ServiceAdapter(val context: Context, val itemLists: Airtime, val key: String) : RecyclerView.Adapter<ServiceAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.d_service_list_design, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemLists.content!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemLists.content!![position]

        try {
            Glide.with(context).load(item.image).into(holder.image)
            holder.title.text = item.name!!
        } catch (e: Exception) { }

        holder.itemView.setOnClickListener {
            when(key){
                Constants.airtime ->{
                    val intent = Intent(context, BuyAirtimeActivity::class.java)
                    intent.putExtra("name", item.name)
                    intent.putExtra("image", item.image)
                    intent.putExtra("service_id", item.serviceID)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
                Constants.data ->{
                    val intent = Intent(context, ServiceVariationActivity::class.java)
                    intent.putExtra("name", item.name)
                    intent.putExtra("key",Constants.data)
                    intent.putExtra("image", item.image)
                    intent.putExtra("service_id", item.serviceID)
                    context.startActivity(intent)
                }
                Constants.education ->{
                    val intent = Intent(context, ServiceVariationActivity::class.java)
                    intent.putExtra("name", item.name)
                    intent.putExtra("image", item.image)
                    intent.putExtra("key",Constants.education)
                    intent.putExtra("service_id", item.serviceID)
                    context.startActivity(intent)
                }
            }
        }
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.service_design_image)
        val title: TextView = itemView.findViewById(R.id.service_design_title)
    }
}