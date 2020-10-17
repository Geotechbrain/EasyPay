package com.geodeveloper.easypay.helper

import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.model.ChargesModel
import com.geodeveloper.paybills.helper.Utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object ChargesUtils {
    //payment gateway charges
    var FLUTTERWAVE_NGN_CHARGE_PERCENT = Constants.FLUTTERWAVE_NGN_CHARGE_PERCENT
    var EASYPAY_NGN_CHARGE_PERCENT = Constants.EASYPAY_NGN_CHARGE_PERCENT
    var FLUTTERWAVE_INTERNATIONAL_PERCENT = Constants.FLUTTERWAVE_INTERNATIONAL_PERCENT
    var EASYPAY_INTERNATIONAL_PERCENT = Constants.EASYPAY_INTERNATIONAL_PERCENT
    var FLUTTERWAVE_MAX_TRANSACTION_FEE = Constants.FLUTTERWAVE_MAX_TRANSACTION_FEE
    var EASYPAY_MAX_TRANSACTION_FEE = Constants.EASYPAY_MAX_TRANSACTION_FEE
    // Tv sub charges
    var TV_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE = Constants.TV_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE
    var TV_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE = Constants.TV_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE
    var TV_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = Constants.TV_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE
    var TV_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = Constants.TV_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE
    // for electricity charges
    var ELECTRICITY_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE = Constants.ELECTRICITY_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE
    var ELECTRICITY_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE = Constants.ELECTRICITY_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE
    var ELECTRICITY_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = Constants.ELECTRICITY_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE
    var ELECTRICITY_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = Constants.ELECTRICITY_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE
    // education charges
    var EDUCATION_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE = Constants.EDUCATION_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE
    var EDUCATION_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE = Constants.EDUCATION_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE
    var EDUCATION_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = Constants.EDUCATION_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE
    var EDUCATION_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = Constants.EDUCATION_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE


    fun getCharges() {
        Utils.databaseRef().child(Constants.charges).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val charges = p0.getValue(ChargesModel::class.java)
                    // payment gateway charges
                    FLUTTERWAVE_NGN_CHARGE_PERCENT = charges!!.FLUTTERWAVE_NGN_CHARGE_PERCENT.toDouble()
                    EASYPAY_NGN_CHARGE_PERCENT = charges.EASYPAY_NGN_CHARGE_PERCENT.toDouble()
                    FLUTTERWAVE_INTERNATIONAL_PERCENT = charges.FLUTTERWAVE_INTERNATIONAL_PERCENT.toDouble()
                    EASYPAY_INTERNATIONAL_PERCENT = charges.EASYPAY_INTERNATIONAL_PERCENT.toDouble()
                    FLUTTERWAVE_MAX_TRANSACTION_FEE = charges.FLUTTERWAVE_MAX_TRANSACTION_FEE.toDouble()
                    EASYPAY_MAX_TRANSACTION_FEE = charges.EASYPAY_MAX_TRANSACTION_FEE.toDouble()
                    //tv sub
                    TV_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE =  charges.TV_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE.toDouble()
                    TV_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE =  charges.TV_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE.toDouble()
                    TV_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE =  charges.TV_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE.toDouble()
                    TV_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE =  charges.TV_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE.toDouble()
                    //electricity
                    ELECTRICITY_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE =  charges.ELECTRICITY_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE.toDouble()
                    ELECTRICITY_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE = charges.ELECTRICITY_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE.toDouble()
                    ELECTRICITY_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = charges.ELECTRICITY_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE.toDouble()
                    ELECTRICITY_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = charges.ELECTRICITY_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE.toDouble()
                    //education charges
                    EDUCATION_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE = charges.EDUCATION_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE.toDouble()
                    EDUCATION_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE =  charges.EDUCATION_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE.toDouble()
                    EDUCATION_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = charges.EDUCATION_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE.toDouble()
                    EDUCATION_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = charges.EDUCATION_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE.toDouble()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}

