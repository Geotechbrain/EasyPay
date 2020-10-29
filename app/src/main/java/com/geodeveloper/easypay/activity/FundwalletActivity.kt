package com.geodeveloper.easypay.activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.paystack.android.Paystack.TransactionCallback
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.model.UsersModel
import com.geodeveloper.paybills.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_buy_airtime.*
import kotlinx.android.synthetic.main.activity_fundwallet.*
import kotlinx.android.synthetic.main.transaction_successful_dialogue.*


class FundwalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fundwallet)
        PaystackSdk.initialize(this)

        fund_wallet_fundBtn.setOnClickListener {
            val amount = fund_wallet_amount.text.toString()
            val cardNumber = fund_wallet_number.text.toString()
            val expYear = fund_wallet_year.text.toString()
            val expMonth = fund_wallet_month.text.toString()
            val cvv = fund_wallet_cvv.text.toString()

            when{
                TextUtils.isEmpty(amount) -> Toast.makeText(this,"please enter amount",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(cardNumber) -> Toast.makeText(this,"please enter card number",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(expYear) -> Toast.makeText(this,"please enter expiry year",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(expMonth) -> Toast.makeText(this,"please enter expiry month",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(cvv) -> Toast.makeText(this,"please enter cvv",Toast.LENGTH_LONG).show()
                amount.toDouble() < 100 ->  Toast.makeText(this,"minimum amount is 100",Toast.LENGTH_LONG).show()
                else -> {
                    Utils.showLoader(this,"processing")
                    pay(amount,cardNumber,expYear,expMonth,cvv)
                }
            }
        }
    }

    private fun pay(amount: String, cardNumber: String, expYear: String, expMonth: String, cvv: String) {
        Utils.databaseRef().child(Constants.paymentGatewayKey).child(Constants.test).child(Constants.publicKey).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val publicKey = p0.value.toString()
                    PaystackSdk.setPublicKey(publicKey)
                    val card = Card(cardNumber, expMonth.toInt(), expYear.toInt(), cvv)
                    if (card.isValid) {
                        val charge = Charge()
                        val transactionRef = System.currentTimeMillis().toString()
                        charge.currency = "NGN"
                        charge.amount = amount.toInt() * 100
                        charge.email = Utils.getUserEmail(this@FundwalletActivity)
                        charge.reference = transactionRef
                        charge.card = card

                        PaystackSdk.chargeCard(this@FundwalletActivity, charge, object : TransactionCallback {
                            override fun onSuccess(transaction: Transaction?) {
                               saveTransaction(Constants.delivered,amount,transactionRef)
                            }

                            override fun beforeValidate(transaction: Transaction?) {
                            }

                            override fun onError(error: Throwable?, transaction: Transaction?) {
                                saveTransaction(Constants.failed, amount, transactionRef)
                            }
                        })
                    } else {
                       Utils.dismissLoader()
                        Toast.makeText(this@FundwalletActivity,"this card is not valid",Toast.LENGTH_LONG).show()
                    }

                }else{
                    Utils.dismissLoader()
                    Toast.makeText(this@FundwalletActivity,"error occur",Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun saveTransaction(status: String, amount: String, transactionRef: String) {
        val nodeID = Utils.databaseRef().push().key.toString()
        val transactionMap = HashMap<String, Any>()
        transactionMap["transaction_type"] = Constants.fundWallet
        transactionMap["transaction_name"] = Constants.fundWallet
        transactionMap["amount"] = amount
        transactionMap["status"] = status
        transactionMap["node_id"] = nodeID
        transactionMap["transaction_id"] = transactionRef
        transactionMap["date"] = System.currentTimeMillis().toString()
        transactionMap["customer_name"] = Utils.getUserName(this)
        transactionMap["customer_email"] = Utils.getUserEmail(this)
        transactionMap["customer_id"] = Utils.currentUserID()
        //save transactions
        Utils.databaseRef().child(Constants.transactions).child(nodeID).setValue(transactionMap).addOnSuccessListener {
            if(status == Constants.delivered){
                Utils.dismissLoader()
                //fund user wallet
                Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()){
                            val user = p0.getValue(UsersModel::class.java)
                            val initialWalletBalance = user!!.wallet_balance.toDouble()
                            val newWalletBalance = initialWalletBalance + amount.toDouble()
                            //save amount to wallet
                            Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child(Constants.walletBalance).setValue(newWalletBalance.toString()).addOnSuccessListener {
                                val mDialogueView = LayoutInflater.from(this@FundwalletActivity).inflate(R.layout.transaction_successful_dialogue, null)
                                val mBuilder = AlertDialog.Builder(this@FundwalletActivity).setView(mDialogueView)
                                val mAlertDualogue = mBuilder.show()
                                mAlertDualogue.transaction_success_dialogue_cancel.setOnClickListener {
                                    mAlertDualogue.dismiss()
                                    finishAndRemoveTask()
                                    Animatoo.animateSwipeRight(this@FundwalletActivity)
                                }
                            }


                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
            }else{
                Utils.dismissLoader()
                Toast.makeText(this,"error occur during transaction",Toast.LENGTH_LONG).show()
            }
        }
    }
}