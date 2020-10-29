package com.geodeveloper.easypay.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.paybills.helper.Utils
import kotlinx.android.synthetic.main.activity_transaction_details.*

class TransactionDetailsActivity : AppCompatActivity() {
    var transactionType: String? = null
    var transactionName: String? = null
    var amount: String? = null
    var status: String? = null
    var transactionID: String? = null
    var date: String? = null
    var beneficiaryNo: String? = null
    var token: String? = null
    var meterNo: String? = null
    var meterHolderName: String? = null
    var meterHolderAddress: String? = null
    var cardNo: String? = null
    var cardHolderName: String? = null
    var isConnectedToInternet: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        transactionType = intent.getStringExtra(Constants.transactionType)
        transactionName = intent.getStringExtra(Constants.transactionName)
        amount = intent.getStringExtra(Constants.amount)
        status = intent.getStringExtra(Constants.transactionStatus)
        transactionID = intent.getStringExtra(Constants.transactionID)
        date = intent.getStringExtra(Constants.date)
        beneficiaryNo = intent.getStringExtra(Constants.number)
        token = intent.getStringExtra(Constants.token)
        meterNo = intent.getStringExtra(Constants.meterNo)
        meterHolderName = intent.getStringExtra(Constants.meterHolderName)
        meterHolderAddress = intent.getStringExtra(Constants.meterHolderAddress)
        cardHolderName = intent.getStringExtra(Constants.cardHolderName)
        cardNo = intent.getStringExtra(Constants.cardNo)

        //basics
        transaction_history_transactioName.text = transactionName
        transaction_history_amount.text = amount
        transaction_history_status.text = status
        transaction_history_ID.text = transactionID
        transaction_history_date.text = DateFormat.format("dd - MM - yyyy hh:mm:ss a", date!!.toLong())

        when (transactionType) {
            Constants.airtime -> {
                transaction_history_beneficiaryNoContainer.visibility = View.VISIBLE
                transaction_history_beneficiaryNo.text = beneficiaryNo
            }
            Constants.data -> {
                transaction_history_beneficiaryNoContainer.visibility = View.VISIBLE
                transaction_history_beneficiaryNo.text = beneficiaryNo
            }
            Constants.education -> {
                transaction_history_beneficiaryNoContainer.visibility = View.VISIBLE
                transaction_history_tokenContainer.visibility = View.VISIBLE
                transaction_history_beneficiaryNo.text = beneficiaryNo
                transaction_history_token.text = token
            }
            Constants.electricity -> {
                transaction_history_beneficiaryNoContainer.visibility = View.VISIBLE
                transaction_history_tokenContainer.visibility = View.VISIBLE
                transaction_history_meterNoContainer.visibility = View.VISIBLE
                transaction_history_meterHolderNameContainer.visibility = View.VISIBLE
                transaction_history_meterHolderAddressContainer.visibility = View.VISIBLE

                transaction_history_beneficiaryNo.text = beneficiaryNo
                transaction_history_token.text = token
                transaction_history_meterNo.text = meterNo
                transaction_history_meterHolderName.text = meterHolderName
                transaction_history_meterHolderAddress.text = meterHolderAddress
            }
            Constants.tv -> {
                transaction_history_beneficiaryNoContainer.visibility = View.VISIBLE
                transaction_history_cardNoContainer.visibility = View.VISIBLE
                transaction_history_cardHolderNameContainer.visibility = View.VISIBLE

                transaction_history_beneficiaryNo.text = beneficiaryNo
                transaction_history_cardNo.text = cardNo
                transaction_history_cardHolderName.text = cardHolderName
            }
        }
        transaction_history_backIcon.setOnClickListener {
            finishAndRemoveTask()
            Animatoo.animateSwipeRight(this)
        }

        transaction_history_copyTransactionID.setOnClickListener {
            Utils.copyValue(this, transaction_history_ID.text.toString())
        }
        transaction_history_copyToken.setOnClickListener {
            Utils.copyValue(this, transaction_history_token.text.toString())
        }
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
        Animatoo.animateSwipeRight(this)
    }
}