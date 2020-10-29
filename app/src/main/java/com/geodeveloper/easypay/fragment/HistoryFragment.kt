package com.geodeveloper.easypay.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.adapter.TransactionHistoryAdapter
import com.geodeveloper.easypay.model.TransactionHistoryModel
import com.geodeveloper.easypay.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.view.*
import java.lang.IllegalStateException

class HistoryFragment : Fragment() {
    private var itemList = ArrayList<TransactionHistoryModel>()
    private var adapter: TransactionHistoryAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        Utils.showLoader(context!!,"loading")
        adapter = TransactionHistoryAdapter(context!!, itemList)
        view.transaction_history_recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        view.transaction_history_recyclerView.layoutManager = layoutManager
        view.transaction_history_recyclerView.adapter = adapter!!

        loadHistory()

        return view
    }

    private fun loadHistory() {
        if (Utils.currentUser() != null) {
            Utils.databaseRef().child(Constants.transactions).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        itemList.clear()
                        for (snapshot in p0.children) {
                            val data = snapshot.getValue(TransactionHistoryModel::class.java)
                            if (data!!.customer_id == Utils.currentUserID()) {
                                itemList.add(data)
                            }
                        }

                        adapter!!.notifyDataSetChanged()
                        try {
                            transaction_history_count.text = "${itemList.size} transactions"
                        }catch (e:IllegalStateException){}
                        Utils.dismissLoader()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(context!!, "Error occur", Toast.LENGTH_LONG).show()
                    try {
                       Utils.dismissLoader()
                    } catch (e: IllegalStateException) {
                    }
                }
            })
        }

    }
}