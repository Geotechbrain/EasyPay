package com.geodeveloper.easypay.service

import com.geodeveloper.easypay.models.airtime.Airtime
import com.geodeveloper.easypay.models.cardVerification.CardVerification
import com.geodeveloper.easypay.models.dataVariation.DataVariation
import com.geodeveloper.easypay.models.paymentResponse.PaymentResponse
import com.geodeveloper.easypay.models.transactionStatus.TransactionResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //get list of airtime available
    @GET("services")
    fun getAirtimeService(@Query("identifier") identifier:String): Call<Airtime>

    @POST("pay")
    fun getPaymentResponse(@Header("Authorization") authkey:String,  @QueryMap queryDetails:HashMap<String,String>): Call<PaymentResponse>

    @GET("service-variations")
    fun getDataVariations(@Query("serviceID") serviceID:String): Call<DataVariation>

    @POST("merchant-verify")
    fun verifyCards(@Header("Authorization") authkey:String, @QueryMap queryDetails:HashMap<String,Any>): Call<CardVerification>

    @POST("requery")
    fun getTransactionStatus(@Header("Authorization") authkey:String, @Query("request_id") requestID:String): Call<TransactionResponse>

}