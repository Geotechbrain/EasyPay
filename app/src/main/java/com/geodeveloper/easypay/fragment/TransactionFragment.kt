package com.geodeveloper.easypay.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.geodeveloper.easypay.R
class TransactionFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
     val view =inflater.inflate(R.layout.fragment_transaction, container, false)


        return view
    }

}