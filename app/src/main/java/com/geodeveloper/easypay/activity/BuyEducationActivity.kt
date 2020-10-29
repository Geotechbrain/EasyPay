package com.geodeveloper.easypay.activity

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
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
import com.geodeveloper.paybills.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_buy_airtime.*
import kotlinx.android.synthetic.main.activity_buy_education.*
import kotlinx.android.synthetic.main.insufficient_fund_dialogue.*
import kotlinx.android.synthetic.main.response_result_dialogue.*
import kotlinx.android.synthetic.main.transaction_successful_dialogue.*
import kotlinx.android.synthetic.main.verify_transaction_dialogue.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuyEducationActivity : AppCompatActivity() {
    var serviceID: String? = null
    var variationCode: String? = null
    var amount: String? = null
    var name: String? = null
    var serviceName: String? = null

    var convenienceFee = 0.0
    var  totalAmount = 0.0
    var transactionResultCode =  ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_education)

        serviceID = intent.getStringExtra("service_id")
        variationCode = intent.getStringExtra("variation_code")
        amount = intent.getStringExtra("amount")
        name = intent.getStringExtra("name")
        serviceName = intent.getStringExtra("service_name")

        buy_education_serviceName.text = name
        buy_education_toolbarTitle.text = "Buy $serviceName"
        buy_education_amount.setText(amount)

        buy_education_buyBtn.setOnClickListener {
            val phoneNumber = buy_education_phoneNumber.text.toString()
            if(phoneNumber.isEmpty()){
                Toast.makeText(this,"Please enter beneficiary phone number",Toast.LENGTH_LONG).show()
            }
            else{
                Utils.showLoader(this,"loading")
                Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()){
                            val currentWalletBalance = p0.value.toString().toDouble()
                            //perform calculations
                            convenienceFee = when(amount!!.toDouble()){
                                in 0.0..999.0 -> ChargesUtils.EDUCATION_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE
                                in 1000.0..4999.0 -> ChargesUtils.EDUCATION_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE
                                in 5000.0..19999.0 -> ChargesUtils.EDUCATION_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE
                                else -> ChargesUtils.EDUCATION_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE
                            }
                            totalAmount = amount!!.toDouble() + convenienceFee
                            if(currentWalletBalance >= totalAmount){
                                Utils.dismissLoader()
                                val mDialogueView2 = LayoutInflater.from(this@BuyEducationActivity).inflate(R.layout.verify_transaction_dialogue, null)
                                val mBuilder2 = AlertDialog.Builder(this@BuyEducationActivity).setView(mDialogueView2)
                                val mAlertDualogue2 = mBuilder2.show()
                                mAlertDualogue2.verify_trans_dialogue_name.text = name
                                mAlertDualogue2.verify_trans_dialogue_amount.text = amount
                                mAlertDualogue2.verify_trans_dialogue_convenienceFee.text = convenienceFee.toString()
                                mAlertDualogue2.verify_trans_dialogue_totalAmount.text = totalAmount.toString()
                                mAlertDualogue2.verify_trans_dialogue_confirmBtn.setOnClickListener {
                                    mAlertDualogue2.dismiss()
                                    Utils.showLoader(this@BuyEducationActivity, "processing transaction")
                                    //deduct from wallet
                                    Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(p0: DataSnapshot) {
                                            if (p0.exists()) {
                                                val initialWalletBalance = p0.value.toString().toDouble()
                                                val newWalletBalance = initialWalletBalance - totalAmount
                                                Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).setValue(newWalletBalance.toString()).addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        performApiTransaction(phoneNumber)
                                                    }else{
                                                        Toast.makeText(this@BuyEducationActivity,"error occur",Toast.LENGTH_LONG).show()
                                                        finishAndRemoveTask()
                                                    }
                                                }
                                            }
                                        }
                                        override fun onCancelled(p0: DatabaseError) {
                                        }
                                    })
                                }

                            }else{
                                Utils.dismissLoader()
                                val mDialogueView = LayoutInflater.from(this@BuyEducationActivity).inflate(R.layout.insufficient_fund_dialogue, null)
                                val mBuilder = AlertDialog.Builder(this@BuyEducationActivity).setView(mDialogueView)
                                val mAlertDualogue = mBuilder.show()
                                mAlertDualogue.insufficient_dialogue_content.text = "You need an extra NGN ${totalAmount - currentWalletBalance} to perfom this transaction, PLEASE FUND YOUR WALLET"
                                mAlertDualogue.insufficient_dialogue_confirmBtn.setOnClickListener {
                                    finish()
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
    private fun performApiTransaction(phoneNumber: String) {
        val requestID =System.currentTimeMillis().toString()
        val query = HashMap<String, String>()
        query["request_id"] = requestID
        query["serviceID"] = serviceID!!
        query["variation_code"] = variationCode!!
        query["amount"] = amount!!
        query["phone"] = phoneNumber

        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getPaymentResponse(ApiAuth.getAuthToken(),query)
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
                            Constants.failed -> saveTransactionInformation(requestID,amount,Constants.failed,0.toString(),purchasedCode)
                            else ->  saveTransactionInformation(requestID,amount,Constants.unknown,0.toString(),purchasedCode)
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
                    var transactionCommision = comission
                    if(transactionCommision == "null"){
                        transactionCommision = 0.0.toString()
                    }
                    val nodeID = Utils.databaseRef().push().key.toString()
                    val transactionMap = HashMap<String, Any>()
                    transactionMap["transaction_type"] = Constants.education
                    transactionMap["transaction_name"] = name!!
                    transactionMap["amount"] = amount
                    transactionMap["beneficiary_number"] = buy_education_phoneNumber.text.toString().trim()
                    transactionMap["status"] = status
                    transactionMap["node_id"] = nodeID
                    transactionMap["transaction_id"] = requestID
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
                                    val mDialogueView = LayoutInflater.from(this@BuyEducationActivity).inflate(R.layout.transaction_successful_dialogue, null)
                                    val mBuilder = AlertDialog.Builder(this@BuyEducationActivity).setView(mDialogueView)
                                    val mAlertDualogue = mBuilder.show()
                                    mAlertDualogue.transaction_success_dialogue_container.visibility = View.VISIBLE
                                    mAlertDualogue.transaction_success_dialogue_token.text = purchasedCode
                                    mAlertDualogue.transaction_success_dialogue_copyToken.setOnClickListener {
                                        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip: ClipData = ClipData.newPlainText("", purchasedCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(this@BuyEducationActivity, "Token copied", Toast.LENGTH_LONG).show()
                                    }
                                    mAlertDualogue.transaction_success_dialogue_cancel.setOnClickListener {
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
                                                    val mDialogueView = LayoutInflater.from(this@BuyEducationActivity).inflate(R.layout.response_result_dialogue, null)
                                                    val mBuilder = AlertDialog.Builder(this@BuyEducationActivity).setView(mDialogueView)
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
                    if(status == Constants.delivered){
                        Toast.makeText(this@BuyEducationActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@BuyEducationActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Utils.dismissLoader()
                finishAndRemoveTask()
                if(status == Constants.delivered){
                    Toast.makeText(this@BuyEducationActivity, "Transaction $status", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@BuyEducationActivity, "Transaction $status", Toast.LENGTH_LONG).show()
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
    }

    override fun onStart() {
        super.onStart()
        getCred()
    }
    inner class getCred: AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg p0: Void?): Void? {
            ChargesUtils.getCharges()
            ApiAuth.getAuthCredentials()
            return null
        }

    }
}