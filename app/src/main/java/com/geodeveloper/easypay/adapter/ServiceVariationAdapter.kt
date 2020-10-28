package com.geodeveloper.easypay.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.activity.BuyDataActivity
import com.geodeveloper.easypay.activity.BuyEducationActivity
import com.geodeveloper.easypay.models.dataVariation.DataVariation

class ServiceVariationAdapter(val context: Context, val itemLists: DataVariation, val key:String) : RecyclerView.Adapter<ServiceVariationAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.data_variation_design, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemLists.content!!.varations!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemLists.content!!.varations!![position]
        holder.title.text = item.name
        holder.itemView.setOnClickListener {
            when(key){
                Constants.data ->{
                    val intent = Intent(context, BuyDataActivity::class.java)
                    intent.putExtra("service_id", itemLists.content.serviceID)
                    intent.putExtra("variation_code", item.variation_code)
                    intent.putExtra("amount", item.variation_amount)
                    intent.putExtra("name", item.name)
                    intent.putExtra("service_name", itemLists.content.ServiceName)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
                Constants.education ->{
                    val intent = Intent(context, BuyEducationActivity::class.java)
                    intent.putExtra("service_id", itemLists.content.serviceID)
                    intent.putExtra("variation_code", item.variation_code)
                    intent.putExtra("amount", item.variation_amount)
                    intent.putExtra("name", item.name)
                    intent.putExtra("service_name", itemLists.content.ServiceName)
                    context.startActivity(intent)
                }
            }

        }
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.data_variation_design_name)
    }
}