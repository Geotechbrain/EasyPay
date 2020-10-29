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
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.activity.TransactionDetailsActivity
import com.geodeveloper.easypay.model.TransactionHistoryModel
import com.geodeveloper.paybills.helper.Utils
import java.lang.Exception

@Suppress("DEPRECATION")
class TransactionHistoryAdapter  (val context: Context, val itemLists: ArrayList<TransactionHistoryModel>): RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.transaction_history_design,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemLists.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val item  = itemLists[position]

        holder.date.text = Utils.formatTime(item.date!!.toLong())
        holder.type.text = item.transaction_type
        holder.amount.text = item.amount
        holder.id.text = item.transaction_id
        if (item.status == Constants.delivered){
            holder.status.setTextColor(context.resources.getColor(android.R.color.holo_green_dark))
        }else{
            holder.status.setTextColor(context.resources.getColor(R.color.colorPrimary))
        }
        holder.status.text = item.status
        when(item.transaction_type){
            Constants.data -> holder.number.text = item.beneficiary_number
            Constants.airtime -> holder.number.text = item.beneficiary_number
            Constants.electricity -> holder.number.text = item.meter_number
            Constants.tv -> holder.number.text = item.card_number
            Constants.education -> holder.number.text = item.beneficiary_number
            Constants.fundWallet -> holder.number.text = ""
            else ->{
                holder.number.text = ""
            }
        }
        holder.itemView.setOnClickListener {
            when(item.transaction_type){
                Constants.fundWallet ->{
                    val intent = Intent(context, TransactionDetailsActivity::class.java)
                    intent.putExtra(Constants.transactionType,item.transaction_type)
                    intent.putExtra(Constants.transactionName,item.transaction_name)
                    intent.putExtra(Constants.amount,item.amount)
                    intent.putExtra(Constants.transactionStatus,item.status)
                    intent.putExtra(Constants.transactionID,item.transaction_id)
                    intent.putExtra(Constants.date,item.date)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
                Constants.airtime ->{
                    val intent = Intent(context,TransactionDetailsActivity::class.java)
                    intent.putExtra(Constants.transactionType,item.transaction_type)
                    intent.putExtra(Constants.transactionName,item.transaction_name)
                    intent.putExtra(Constants.amount,item.amount)
                    intent.putExtra(Constants.transactionStatus,item.status)
                    intent.putExtra(Constants.transactionID,item.transaction_id)
                    intent.putExtra(Constants.number,item.beneficiary_number)
                    intent.putExtra(Constants.date,item.date)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
                Constants.data ->{
                    val intent = Intent(context,TransactionDetailsActivity::class.java)
                    intent.putExtra(Constants.transactionType,item.transaction_type)
                    intent.putExtra(Constants.transactionName,item.transaction_name)
                    intent.putExtra(Constants.amount,item.amount)
                    intent.putExtra(Constants.transactionStatus,item.status)
                    intent.putExtra(Constants.transactionID,item.transaction_id)
                    intent.putExtra(Constants.number,item.beneficiary_number)
                    intent.putExtra(Constants.date,item.date)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
                Constants.education ->{
                    val intent = Intent(context,TransactionDetailsActivity::class.java)
                    intent.putExtra(Constants.transactionType,item.transaction_type)
                    intent.putExtra(Constants.transactionName,item.transaction_name)
                    intent.putExtra(Constants.amount,item.amount)
                    intent.putExtra(Constants.transactionStatus,item.status)
                    intent.putExtra(Constants.transactionID,item.transaction_id)
                    intent.putExtra(Constants.token,item.TOKEN)
                    intent.putExtra(Constants.number,item.beneficiary_number)
                    intent.putExtra(Constants.date,item.date)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
                Constants.electricity ->{
                    val intent = Intent(context,TransactionDetailsActivity::class.java)
                    intent.putExtra(Constants.transactionType,item.transaction_type)
                    intent.putExtra(Constants.transactionName,item.transaction_name)
                    intent.putExtra(Constants.amount,item.amount)
                    intent.putExtra(Constants.transactionStatus,item.status)
                    intent.putExtra(Constants.transactionID,item.transaction_id)
                    intent.putExtra(Constants.number,item.beneficiary_number)
                    intent.putExtra(Constants.meterNo,item.meter_number)
                    intent.putExtra(Constants.meterHolderName,item.meter_holder_name)
                    intent.putExtra(Constants.meterHolderAddress,item.meter_holder_address)
                    intent.putExtra(Constants.token,item.TOKEN)
                    intent.putExtra(Constants.date,item.date)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
                Constants.tv ->{
                    val intent = Intent(context,TransactionDetailsActivity::class.java)
                    intent.putExtra(Constants.transactionType,item.transaction_type)
                    intent.putExtra(Constants.transactionName,item.transaction_name)
                    intent.putExtra(Constants.amount,item.amount)
                    intent.putExtra(Constants.transactionStatus,item.status)
                    intent.putExtra(Constants.transactionID,item.transaction_id)
                    intent.putExtra(Constants.token,item.TOKEN)
                    intent.putExtra(Constants.cardNo,item.card_number)
                    intent.putExtra(Constants.number,item.beneficiary_number)
                    intent.putExtra(Constants.cardHolderName,item.card_holder_name)
                    intent.putExtra(Constants.date,item.date)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
            }
        }
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.history_design_date)
        val type: TextView = itemView.findViewById(R.id.history_design_type)
        val number: TextView = itemView.findViewById(R.id.history_design_phoneNumber)
        val amount: TextView = itemView.findViewById(R.id.history_design_amount)
        val status: TextView = itemView.findViewById(R.id.history_design_status)
        val id: TextView = itemView.findViewById(R.id.history_design_id)


    }
}