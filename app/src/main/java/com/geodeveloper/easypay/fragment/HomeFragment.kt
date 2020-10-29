package com.geodeveloper.easypay.fragment

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.activity.FundwalletActivity
import com.geodeveloper.easypay.adapter.ServiceAdapter
import com.geodeveloper.easypay.models.airtime.Airtime
import com.geodeveloper.easypay.service.ApiService
import com.geodeveloper.easypay.service.ServiceBuilder
import com.geodeveloper.paybills.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         val view = inflater.inflate(R.layout.fragment_home, container, false)
        Utils.showLoader(context!!,"loading")
        view.main_airtimeList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        view.main_airtimeList.layoutManager = layoutManager

        //for data list
        view.main_dataList.setHasFixedSize(true)
        val dataLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        view.main_dataList.layoutManager = dataLayoutManager

        //for education list
        view.main_educationList.setHasFixedSize(true)
        val eduLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        view.main_educationList.layoutManager = eduLayoutManager

        //for tvCable list
        view.main_tvCableList.setHasFixedSize(true)
        val tvLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        view.main_tvCableList.layoutManager = tvLayoutManager

        //for electriciy list
        view.main_electricityList.setHasFixedSize(true)
        val electricityLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        view.main_electricityList.layoutManager = electricityLayoutManager

        view.main_fund_wallet.setOnClickListener {
            startActivity(Intent(context, FundwalletActivity::class.java))
        }

        loadCurrentUserWalletBalance()
        getAirtimeList()
        getDataList()
        getEducationList()
        getTVCableList()
        getElectricity()

        return view
    }

    private fun getAirtimeList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("airtime")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_airtimeList.adapter = ServiceAdapter(context!!, itemLists, Constants.airtime)
                } else {
                    Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getDataList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("data")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_dataList.adapter = ServiceAdapter(context!!, itemLists, Constants.data)
                } else {
                    Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getEducationList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("education")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_educationList.adapter = ServiceAdapter(context!!, itemLists, Constants.education)
                } else {
                    Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getTVCableList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("tv-subscription")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_tvCableList.adapter = ServiceAdapter(context!!, itemLists, Constants.tv)
                } else {
                    Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getElectricity() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("electricity-bill")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_electricityList.adapter = ServiceAdapter(context!!, itemLists, Constants.electricity)
                    Utils.dismissLoader()
                } else {
                    Utils.dismissLoader()
                    Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Utils.dismissLoader()
                Toast.makeText(context, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadCurrentUserWalletBalance() {
        Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child("wallet_balance").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    try {
                        main_walletAmount.text = "NGN ${p0.value.toString().toDouble()}"
                    }catch (e:IllegalStateException){}

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}