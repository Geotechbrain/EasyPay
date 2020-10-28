package com.geodeveloper.easypay.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.adapter.ServiceVariationAdapter
import com.geodeveloper.easypay.models.dataVariation.DataVariation
import com.geodeveloper.easypay.service.ApiService
import com.geodeveloper.easypay.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_data_variation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceVariationActivity : AppCompatActivity() {
    var serviceID: String? = null
    var name: String? = null
    var image: String? = null
    var key:String? = null
    var isConnectedToInternet:Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_variation)

        serviceID = intent.getStringExtra("service_id")
        name = intent.getStringExtra("name")
        image = intent.getStringExtra("image")
        key = intent.getStringExtra("key")

        data_list_activity_recylerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        data_list_activity_recylerView.layoutManager = layoutManager

        data_list_activity_toolbarTitle.text = name
        Glide.with(this).load(image!!).into(data_list_activity_image)

    }

    private fun getDataVariations() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getDataVariations(serviceID!!)
        requestCall.enqueue(object : Callback<DataVariation> {
            override fun onResponse(call: Call<DataVariation>, response: Response<DataVariation>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    data_list_activity_recylerView.adapter = ServiceVariationAdapter(this@ServiceVariationActivity, itemLists, key!!)

                } else {
                    Toast.makeText(this@ServiceVariationActivity, "Error occur", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<DataVariation>, t: Throwable) {
                Toast.makeText(this@ServiceVariationActivity, "Error occur", Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        getDataVariations()
    }
}