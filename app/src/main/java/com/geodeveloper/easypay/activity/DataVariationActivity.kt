package com.geodeveloper.easypay.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.adapter.ServiceVariationAdapter
import com.geodeveloper.easypay.models.dataVariation.DataVariation
import com.geodeveloper.easypay.service.ApiService
import com.geodeveloper.easypay.service.ServiceBuilder
import com.geodeveloper.paybills.helper.Utils
import kotlinx.android.synthetic.main.activity_data_variation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException

class DataVariationActivity : AppCompatActivity() {
    var serviceID: String? = null
    var name: String? = null
    var image: String? = null
    var isConnectedToInternet:Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_variation)

        serviceID = intent.getStringExtra("service_id")
        name = intent.getStringExtra("name")
        image = intent.getStringExtra("image")

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
                    data_list_activity_recylerView.adapter = ServiceVariationAdapter(this@DataVariationActivity, itemLists, Constants.data)

                } else {
                    Toast.makeText(this@DataVariationActivity, "Error occur", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<DataVariation>, t: Throwable) {
                Toast.makeText(this@DataVariationActivity, "Error occur", Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        getDataVariations()
    }
}