package com.geodeveloper.easypay

object Constants {
    //firebase paths
    const val charges =  "charges"
    const val apiAuth = "api_auth_credentials"
    const val live = "live"
    const val test = "test"
    const val users = "user"


    //charges
    const val FLUTTERWAVE_NGN_CHARGE_PERCENT = 1.4
    const val EASYPAY_NGN_CHARGE_PERCENT = 2.0
    const val FLUTTERWAVE_INTERNATIONAL_PERCENT = 3.8
    const val EASYPAY_INTERNATIONAL_PERCENT = 4.0
    const val FLUTTERWAVE_MAX_TRANSACTION_FEE = 2000.0
    const val EASYPAY_MAX_TRANSACTION_FEE = 2200.0
    // Tv sub payment charges
    const val TV_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE = 50.0
    const val TV_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE = 100.0
    const val TV_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = 100.0
    const val TV_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = 200.0
    // electricity charges
    const val ELECTRICITY_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE = 50.0
    const val ELECTRICITY_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE = 100.0
    const val ELECTRICITY_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = 100.0
    const val ELECTRICITY_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = 200.0

    // education charges
    const val EDUCATION_PAYMENT_LESS_THAN_1000_CONVENIENCE_FEE = 50.0
    const val EDUCATION_PAYMENT_BETWEEN_1000_AND_5000_CONVENIENCE_FEE = 100.0
    const val EDUCATION_PAYMENT_BETWEEN_5000_AND_20000_CONVENIENCE_FEE = 100.0
    const val EDUCATION_PAYMENT_GREATER_THAN_20000_CONVENIENCE_FEE = 200.0

}