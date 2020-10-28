package com.geodeveloper.easypay.activity

import android.app.AlertDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.helper.ChargesUtils
import com.geodeveloper.easypay.model.UsersModel
import com.geodeveloper.easypay.models.paymentResponse.PaymentResponse
import com.geodeveloper.easypay.models.transactionStatus.TransactionResponse
import com.geodeveloper.easypay.service.ApiAuth
import com.geodeveloper.easypay.service.ApiService
import com.geodeveloper.easypay.service.ServiceBuilder
import com.geodeveloper.easypay.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_buy_data.*
import kotlinx.android.synthetic.main.insufficient_fund_dialogue.*
import kotlinx.android.synthetic.main.response_result_dialogue.*
import kotlinx.android.synthetic.main.transaction_successful_dialogue.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuyDataActivity : AppCompatActivity() {
    var serviceID:String? = null
    var variationCode:String? = null
    var amount:String? = null
    var name:String? = null
    var serviceName:String? = null

    var transactionResultCode =  ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_data)
        serviceID = intent.getStringExtra("service_id")
        variationCode = intent.getStringExtra("variation_code")
        amount = intent.getStringExtra("amount")
        name = intent.getStringExtra("name")
        serviceName = intent.getStringExtra("service_name")

        buy_data_name.text = name
        buy_data_toolbarTitle.text = "Buy $serviceName"
        buy_data_amount.setText(amount)

        buy_data_buyBtn.setOnClickListener {
            if(amount != null){
                val phoneNumber = buy_data_number.text.toString().trim()
                if (phoneNumber.isEmpty()){
                    Toast.makeText(this,"please enter the phone number",Toast.LENGTH_LONG).show()
                }
                else{
                    Utils.showLoader(this,"checking wallet balance")
                    //verify that the current wallet balance is enough
                    Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.exists()){
                                val currentBalance = p0.value.toString().toDouble()
                                if(currentBalance >= amount!!.toDouble()){
                                    Utils.dismissLoader()
                                    Utils.showLoader(this@BuyDataActivity,"processing transaction")
                                    //deduct from wallet
                                    Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(p0: DataSnapshot) {
                                            if (p0.exists()) {
                                                val initialWalletBalance = p0.value.toString().toDouble()
                                                val newWalletBalance = initialWalletBalance - amount!!.toDouble()
                                                Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).setValue(newWalletBalance.toString()).addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        performApiTransaction(phoneNumber)
                                                    }else{
                                                        Toast.makeText(this@BuyDataActivity,"error occur", Toast.LENGTH_LONG).show()
                                                        finishAndRemoveTask()
                                                    }
                                                }
                                            }
                                        }
                                        override fun onCancelled(p0: DatabaseError) {
                                        }
                                    })
                                }else{
                                  Utils.dismissLoader()
                                    val mDialogueView = LayoutInflater.from(this@BuyDataActivity).inflate(R.layout.insufficient_fund_dialogue, null)
                                    val mBuilder = AlertDialog.Builder(this@BuyDataActivity).setView(mDialogueView)
                                    val mAlertDualogue = mBuilder.show()
                                    mAlertDualogue.insufficient_dialogue_content.text = "You need an extra NGN ${amount!!.toDouble() - currentBalance} to perfom this transaction, PLEASE FUND YOUR WALLET"
                                    mAlertDualogue.insufficient_dialogue_confirmBtn.setOnClickListener {
                                        mAlertDualogue.dismiss()
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

    private fun performApiTransaction(phoneNumber: String) {
        val requestID = System.currentTimeMillis().toString()
        val query = HashMap<String, String>()
        query["request_id"] = requestID
        query["serviceID"] = serviceID!!
        query["billersCode"] = phoneNumber
        query["variation_code"] = variationCode!!
        query["amount"] = amount!!
        query["phone"] = phoneNumber

        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getPaymentResponse(ApiAuth.getAuthToken(),query)
        requestCall.enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                if (response.isSuccessful) {
                    transactionResultCode = response.body()!!.code!!
                    when(response.body()!!.code){
                        Constants.TRANSACTION_PROCESSED ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!)
                                }
                            }, 5000)
                        }
                        Constants.TRANSACTION_PROCESSING ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!)
                                }
                            }, 5000)
                        }
                        Constants.CODE_TRANSACTION_FAILED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.TRANSACTION_QUERY ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!)
                                }
                            }, 5000)
                        }
                        Constants.CODE_VARIATION_NOT_EXIST ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.INVALID_ARGUMENT ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.CODE_PRODCUT_NOT_EXTIST ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.CODE_AMOUNT_BELOW_MINIMUM ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.REQUEST_ID_ALREADY_EXIST ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.INVALID_REQUEST_ID ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.CODE_AMOUNT_ABOVE_MAXIMUM ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.CODE_LOW_WALLET_BALANCE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.CODE_LIKELY_DUPLICATE_TRANSACTION ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.ACCOUNT_LOCKED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.ACCOUNT_SUSPENDED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.NOT_HAVE_API_ACCESS ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.ACCOUNT_INACTIVE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.BILLER_NOT_REACHABLE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.BELOW_MINIUM_QTY ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.ABOVE_MAXIMUM_QTY ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.SERVICE_SUSPENDED ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        Constants.SERVICE_INACTIVE ->{
                            saveTransactionInformation(requestID, amount!!, Constants.failed,0.toString())
                        }
                        else ->{
                            Handler().postDelayed(object : Runnable {
                                override fun run() {
                                    getTransactionStatus(requestID,amount!!)
                                }
                            }, 5000)
                        }
                    }
                }else{
                    saveTransactionInformation(requestID, amount!!, Constants.unknown,0.toString())
                }
            }
            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                saveTransactionInformation(requestID, amount!!, Constants.unknown,0.toString())
            }
        })
    }
    private fun getTransactionStatus(requestID: String, amount: String) {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getTransactionStatus( ApiAuth.getAuthToken(),requestID)
        requestCall.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    val status = response.body()!!
                    transactionResultCode = status.code.toString()
                    if(status.content!!.transactions!!.status != null){
                        when(status.content.transactions!!.status){
                            Constants.delivered -> saveTransactionInformation(requestID,amount,Constants.delivered,status.content.transactions!!.commission.toString())
                            Constants.failed ->  saveTransactionInformation(requestID,amount,Constants.failed,0.toString())
                            else ->  saveTransactionInformation(requestID,amount,Constants.unknown,0.toString())
                        }
                    }else{
                        saveTransactionInformation(requestID,amount,Constants.unknown,0.toString())
                    }
                }else{
                    saveTransactionInformation(requestID,amount,Constants.unknown,0.toString())
                }
            }
            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                saveTransactionInformation(requestID,amount,Constants.unknown,0.toString())
            }
        })
    }
    private fun saveTransactionInformation(requestID: String, amount: String, status: String,comission:String) {
        Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue(UsersModel::class.java)
                    var transactionCommision = comission
                    if(transactionCommision == "null"){
                        transactionCommision = 0.0.toString()
                    }
                    val nodeID = Utils.databaseRef().push().key.toString()
                    val transactionMap = HashMap<String, Any>()
                    transactionMap["transaction_type"] = Constants.data
                    transactionMap["transaction_name"] = name!!
                    transactionMap["amount"] = amount
                    transactionMap["beneficiary_number"] = buy_data_number.text.toString().trim()
                    transactionMap["status"] = status
                    transactionMap["node_id"] = nodeID
                    transactionMap["transaction_id"] = requestID
                    transactionMap["date"] = System.currentTimeMillis().toString()
                    transactionMap["customer_name"] = user!!.fullname!!
                    transactionMap["customer_email"] = user.email!!
                    transactionMap["customer_number"] = user.phone_number!!
                    transactionMap["customer_id"] = user.user_id!!
                    transactionMap["commission"] =transactionCommision
                    transactionMap["gain"] = transactionCommision

                    Utils.databaseRef().child(Constants.transactions).child(nodeID).setValue(transactionMap).addOnCompleteListener { setTransactionInfo ->
                        if (setTransactionInfo.isSuccessful) {
                            when(status){
                                Constants.delivered ->{
                                    Utils.dismissLoader()
                                    val mDialogueView = LayoutInflater.from(this@BuyDataActivity).inflate(R.layout.transaction_successful_dialogue, null)
                                    val mBuilder = AlertDialog.Builder(this@BuyDataActivity).setView(mDialogueView)
                                    val mAlertDualogue = mBuilder.show()
                                    mAlertDualogue.transaction_success_dialogue_cancel.setOnClickListener {
                                        mAlertDualogue.dismiss()
                                        finishAndRemoveTask()
                                    }

                                }
                                Constants.failed ->{
                                    //refund user
                                    Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object :ValueEventListener{
                                        override fun onDataChange(p0: DataSnapshot) {
                                            val initialWalletBalance = p0.value.toString().toDouble()
                                            val newWalletBalance = initialWalletBalance + amount.toDouble()
                                            Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).setValue(newWalletBalance.toString()).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    Utils.dismissLoader()
                                                    val mDialogueView = LayoutInflater.from(this@BuyDataActivity).inflate(R.layout.response_result_dialogue, null)
                                                    val mBuilder = AlertDialog.Builder(this@BuyDataActivity).setView(mDialogueView)
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
                                                        finishAndRemoveTask()
                                                    }
                                                }
                                            }
                                        }
                                        override fun onCancelled(p0: DatabaseError) {}
                                    })
                                }
                                Constants.unknown ->{
                                    Utils.dismissLoader()
                                    finish()
                                }
                            }
                        }
                    }
                }else{
                   Utils.dismissLoader()
                    finishAndRemoveTask()
                    Toast.makeText(this@BuyDataActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Utils.dismissLoader()
                finishAndRemoveTask()
                Toast.makeText(this@BuyDataActivity, "Transaction $status", Toast.LENGTH_LONG).show()
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
    inner class getCred:AsyncTask<Void,Void,Void>(){
        override fun doInBackground(vararg p0: Void?): Void? {
            ChargesUtils.getCharges()
            ApiAuth.getAuthCredentials()
            return null
        }

    }

}