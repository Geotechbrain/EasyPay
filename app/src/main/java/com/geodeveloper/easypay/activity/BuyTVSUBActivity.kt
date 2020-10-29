package com.geodeveloper.easypay.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.helper.ChargesUtils
import com.geodeveloper.easypay.model.UsersModel
import com.geodeveloper.easypay.models.cardVerification.CardVerification
import com.geodeveloper.easypay.models.paymentResponse.PaymentResponse
import com.geodeveloper.easypay.models.transactionStatus.TransactionResponse
import com.geodeveloper.easypay.service.ApiAuth
import com.geodeveloper.easypay.service.ApiService
import com.geodeveloper.easypay.service.ServiceBuilder
import com.geodeveloper.easypay.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_buy_t_v_s_u_b.*
import kotlinx.android.synthetic.main.d_card_verificcation_result_dialogue.*
import kotlinx.android.synthetic.main.insufficient_fund_dialogue.*
import kotlinx.android.synthetic.main.response_result_dialogue.*
import kotlinx.android.synthetic.main.transaction_successful_dialogue.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuyTVSUBActivity : AppCompatActivity() {
    var serviceID: String? = null
    var variationCode: String? = null
    var amount: String? = null
    var name: String? = null
    var serviceName: String? = null

    var customeName:String? = null
    var isConnectedToInternet:Boolean? = null

    var  totalAmount = 0.0
    var convenienceFee = 0.0
    var transactionResultCode =  ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_t_v_s_u_b)

        serviceID = intent.getStringExtra("service_id")
        variationCode = intent.getStringExtra("variation_code")
        amount = intent.getStringExtra("amount")
        name = intent.getStringExtra("name")
        serviceName = intent.getStringExtra("service_name")

        buy_dstv_name.text = name
        buy_dstv_toolbarTitle.text = "Buy $serviceName"
        buy_dstv_amount.setText(amount)

        buy_dstv_buyBtn.setOnClickListener {
            val cardNumber = buy_dstv_cardNumber.text.toString().trim()
            val phoneNumber = buy_dstv_number.text.toString().trim()
            when {
                TextUtils.isEmpty(cardNumber) -> Toast.makeText(this, "Please provide card number", Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(phoneNumber) -> Toast.makeText(this, "phone number is required", Toast.LENGTH_LONG).show()
                else -> {
                    Utils.showLoader(this,"loading")
                    Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                val currentWalletBalance = p0.value.toString().toDouble()
                                //perform calculations
                                convenienceFee = when(amount!!.toDouble()){
                                    in 0.0..999.0 -> ChargesUtils.TV_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE
                                    in 1000.0..4999.0 -> ChargesUtils.TV_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE
                                    in 5000.0..19999.0 -> ChargesUtils.TV_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE
                                    else -> ChargesUtils.TV_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE
                                }
                                totalAmount = amount!!.toDouble() + convenienceFee
                                if(currentWalletBalance > totalAmount){
                                    verifySmartCard(cardNumber, phoneNumber)
                                } else {
                                    Utils.dismissLoader()
                                    val mDialogueView = LayoutInflater.from(this@BuyTVSUBActivity).inflate(R.layout.insufficient_fund_dialogue, null)
                                    val mBuilder = AlertDialog.Builder(this@BuyTVSUBActivity).setView(mDialogueView)
                                    val mAlertDualogue = mBuilder.show()
                                    mAlertDualogue.insufficient_dialogue_content.text = "You need an extra NGN ${totalAmount - currentWalletBalance} to perfom this transaction, PLEASE FUND YOUR WALLET"
                                    mAlertDualogue.insufficient_dialogue_confirmBtn.setOnClickListener {
                                        startActivity(Intent(this@BuyTVSUBActivity, FundwalletActivity::class.java))
                                        finishAndRemoveTask()
                                        Animatoo.animateSwipeLeft(this@BuyTVSUBActivity)
                                    }
                                }
                            }
                        }
                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })
                }
            }
        }
    }

    private fun verifySmartCard(cardNumber: String, phoneNumber: String) {
        val query = HashMap<String, Any>()
        query["billersCode"] = cardNumber
        query["serviceID"] = serviceID!!

        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.verifyCards(ApiAuth.getAuthToken(), query)
        requestCall.enqueue(object : Callback<CardVerification> {
            override fun onResponse(call: Call<CardVerification>, response: Response<CardVerification>) {
                if (response.isSuccessful) {
                    Utils.dismissLoader()
                    val result = response.body()!!
                    if (result.content!!.Customer_Name != null) {
                        val mDialogueView = LayoutInflater.from(this@BuyTVSUBActivity).inflate(R.layout.d_card_verificcation_result_dialogue, null)
                        val mBuilder = AlertDialog.Builder(this@BuyTVSUBActivity).setView(mDialogueView)
                        val mAlertDualogue = mBuilder.show()
                        mAlertDualogue.card_verification_result_name.text = result.content.Customer_Name
                        mAlertDualogue.card_verification_result_ID.text = result.content.Customer_ID
                        mAlertDualogue.card_verification_result_dueDate.text = result.content.DUE_DATE
                        mAlertDualogue.card_verification_result_amount.text = amount
                        mAlertDualogue.card_verification_result_convenienceFee.text = convenienceFee.toString()
                        mAlertDualogue.card_verification_result_totalAmount.text = totalAmount.toString()
                        mAlertDualogue.card_verification_result_confirmBtn.setOnClickListener {
                            mAlertDualogue.cancel()
                            customeName = result.content.Customer_Name
                           Utils.showLoader(this@BuyTVSUBActivity,"processing")
                            //deduct from wallet
                            Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(p0: DataSnapshot) {
                                    if (p0.exists()) {
                                        val initialWalletBalance = p0.value.toString().toDouble()
                                        val newWalletBalance = initialWalletBalance - totalAmount
                                        Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).setValue(newWalletBalance.toString()).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                makePayment(cardNumber, phoneNumber)
                                            }else{
                                                Toast.makeText(this@BuyTVSUBActivity,"error occur",
                                                    Toast.LENGTH_LONG).show()
                                                finishAndRemoveTask()
                                            }
                                        }
                                    }
                                }
                                override fun onCancelled(p0: DatabaseError) {
                                }
                            })
                        }
                    } else {
                        Utils.dismissLoader()
                        Toast.makeText(this@BuyTVSUBActivity, "Card verification failed", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CardVerification>, t: Throwable) {
                Utils.dismissLoader()
                Toast.makeText(this@BuyTVSUBActivity, "Card verification failed", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun makePayment(cardNumber: String, phoneNumber: String) {
        val requestID = System.currentTimeMillis().toString()
        val query = HashMap<String, String>()
        query["request_id"] = requestID
        query["serviceID"] = serviceID!!
        query["billersCode"] = cardNumber
        query["variation_code"] = variationCode!!
        query["amount"] = amount!!
        query["phone"] = phoneNumber

        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getPaymentResponse(ApiAuth.getAuthToken(), query)
        requestCall.enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    transactionResultCode = response.body()!!.code!!
                    when(response.body()!!.code){
                        Constants.TRANSACTION_PROCESSED ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!,result.purchased_code)
                                }
                            }, 5000)
                        }
                        Constants.TRANSACTION_PROCESSING ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!,result.purchased_code)
                                }
                            }, 5000)
                        }
                        Constants.CODE_TRANSACTION_FAILED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.TRANSACTION_QUERY ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!,result.purchased_code)
                                }
                            }, 5000)
                        }
                        Constants.CODE_VARIATION_NOT_EXIST ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.INVALID_ARGUMENT ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.CODE_PRODCUT_NOT_EXTIST ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.CODE_AMOUNT_BELOW_MINIMUM ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.REQUEST_ID_ALREADY_EXIST ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.INVALID_REQUEST_ID ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.CODE_AMOUNT_ABOVE_MAXIMUM ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.CODE_LOW_WALLET_BALANCE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.CODE_LIKELY_DUPLICATE_TRANSACTION ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.ACCOUNT_LOCKED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.ACCOUNT_SUSPENDED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.NOT_HAVE_API_ACCESS ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.ACCOUNT_INACTIVE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.BILLER_NOT_REACHABLE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.BELOW_MINIUM_QTY ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.ABOVE_MAXIMUM_QTY ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.SERVICE_SUSPENDED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        Constants.SERVICE_INACTIVE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString(),0.toString())
                        }
                        else ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!,result.purchased_code)
                                }
                            }, 5000)
                        }
                    }
                }else{
                    saveTransactionInformation(requestID, amount!!, Constants.unknown,0.toString(),0.toString())
                }
            }
            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                saveTransactionInformation(requestID, amount!!, Constants.unknown,0.toString(),0.toString())
            }
        })
    }

    private fun getTransactionStatus(requestID: String, amount: String, purchasedCode: String?) {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getTransactionStatus( ApiAuth.getAuthToken(),requestID)
        requestCall.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    val status = response.body()!!
                    transactionResultCode = status.code.toString()
                    if(status.content!!.transactions!!.status != null){
                        when(status.content.transactions!!.status){
                            Constants.delivered -> saveTransactionInformation(requestID,amount,Constants.delivered,status.content.transactions!!.commission.toString(),purchasedCode)
                            Constants.failed -> saveTransactionInformation(requestID,amount,Constants.failed,status.content.transactions!!.commission.toString(),purchasedCode)
                            else -> saveTransactionInformation(requestID,amount,Constants.unknown,status.content.transactions!!.commission.toString(),purchasedCode)
                        }
                    }else{
                        saveTransactionInformation(requestID,amount,Constants.unknown,0.toString(),purchasedCode)
                    }

                }else{
                    saveTransactionInformation(requestID,amount,Constants.unknown,0.toString(),purchasedCode)
                }
            }
            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                saveTransactionInformation(requestID,amount,Constants.unknown,0.toString(),purchasedCode)
            }
        })

    }

    private fun saveTransactionInformation(requestID: String, amount: String, status: String,comission:String,purchasedCode: String?) {
        Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue(UsersModel::class.java)
                    val nodeID = Utils.databaseRef().push().key.toString()
                    val transactionMap = HashMap<String, Any>()
                    var transactionCommision = comission
                    if(transactionCommision == "null"){
                        transactionCommision = 0.0.toString()
                    }
                    transactionMap["transaction_type"] = Constants.tv
                    transactionMap["transaction_name"] = name!!
                    transactionMap["amount"] = amount
                    transactionMap["beneficiary_number"] = buy_dstv_number.text.toString()
                    transactionMap["card_number"] =  buy_dstv_cardNumber.text.toString()
                    transactionMap["card_holder_name"] = customeName!!
                    transactionMap["status"] = status
                    transactionMap["transaction_id"] = requestID
                    transactionMap["node_id"] = nodeID
                    transactionMap["TOKEN"] = purchasedCode!!
                    transactionMap["date"] = System.currentTimeMillis().toString()
                    transactionMap["customer_name"] = user!!.fullname!!
                    transactionMap["customer_email"] = user.email!!
                    transactionMap["customer_number"] = user.phone_number!!
                    transactionMap["customer_id"] = user.user_id!!
                    transactionMap["commission"] =transactionCommision
                    transactionMap["gain"] = (transactionCommision.toDouble() + convenienceFee).toString()

                    Utils.databaseRef().child(Constants.transactions).child(nodeID).setValue(transactionMap).addOnCompleteListener { setTransactionInfo ->
                        if (setTransactionInfo.isSuccessful) {
                            when(status){
                                Constants.delivered ->{
                                    Utils.dismissLoader()
                                    val mDialogueView = LayoutInflater.from(this@BuyTVSUBActivity).inflate(R.layout.transaction_successful_dialogue, null)
                                    val mBuilder = AlertDialog.Builder(this@BuyTVSUBActivity).setView(mDialogueView)
                                    val mAlertDualogue = mBuilder.show()
                                   mAlertDualogue.transaction_success_dialogue_cancel.setOnClickListener {
                                       mAlertDualogue.cancel()
                                       finish()

                                   }
                                }
                                Constants.failed ->{
                                    //refund user
                                    Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object :ValueEventListener{
                                        override fun onDataChange(p0: DataSnapshot) {
                                            val initialWalletBalance = p0.value.toString().toDouble()
                                            val newWalletBalance = initialWalletBalance + totalAmount
                                            Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).setValue(newWalletBalance.toString()).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    Utils.dismissLoader()
                                                    val mDialogueView = LayoutInflater.from(this@BuyTVSUBActivity).inflate(R.layout.response_result_dialogue, null)
                                                    val mBuilder = AlertDialog.Builder(this@BuyTVSUBActivity).setView(mDialogueView)
                                                    val mAlertDualogue = mBuilder.show()
                                                    when(transactionResultCode){
                                                        Constants.CODE_TRANSACTION_FAILED ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, please try again"
                                                        }
                                                        Constants.CODE_PRODCUT_NOT_EXTIST ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, product not exist"
                                                        }
                                                        Constants.CODE_VARIATION_NOT_EXIST ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, variation not exist"
                                                        }
                                                        Constants.CODE_AMOUNT_BELOW_MINIMUM ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, amount below minimum"
                                                        }
                                                        Constants.CODE_AMOUNT_ABOVE_MAXIMUM ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, amount above maximum"
                                                        }
                                                        Constants.CODE_LIKELY_DUPLICATE_TRANSACTION ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, processing many transaction at the moment please try again now"
                                                        }
                                                        Constants.CODE_BILLERS_SERVICE_UNREACHABLE ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, $serviceID server is curreny unreachable please try again"
                                                        }
                                                        Constants.CODE_SERVICE_SUSPENDED ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, $serviceID service suspended please try again later"
                                                        }
                                                        Constants.CODE_SERVICE_INACTIVE ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, $serviceID service is inactive, please try again later"
                                                        }
                                                        Constants.CODE_SYSTEM_ERROR->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, server error occured, please contact us"
                                                        }
                                                        else ->{
                                                            mAlertDualogue.result_dialogue_message.text ="Transaction failed, please try again"
                                                        }
                                                    }
                                                    mAlertDualogue.result_dialogue_btn.setOnClickListener {
                                                        mAlertDualogue.dismiss()
                                                        finish()
                                                    }
                                                }
                                            }
                                        }
                                        override fun onCancelled(p0: DatabaseError) {}
                                    })
                                }
                                Constants.unknown ->{
                                    Utils.dismissLoader()
                                    finishAndRemoveTask()
                                }
                            }
                        }
                    }
                }else{
                    Utils.dismissLoader()
                    finishAndRemoveTask()
                    if(status == Constants.delivered){
                        Toast.makeText(this@BuyTVSUBActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@BuyTVSUBActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Utils.dismissLoader()
                finishAndRemoveTask()
                if(status == Constants.delivered){
                    Toast.makeText(this@BuyTVSUBActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@BuyTVSUBActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    override fun onBackPressed() {
        finishAndRemoveTask()
        Animatoo.animateSwipeRight(this)
    }
    override fun onResume() {
        super.onResume()
       getCred().execute()
    }
    inner class getCred: AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg p0: Void?): Void? {
            ChargesUtils.getCharges()
            ApiAuth.getAuthCredentials()
            return null
        }

    }
}