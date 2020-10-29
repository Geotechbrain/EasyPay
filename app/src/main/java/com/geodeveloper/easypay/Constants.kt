package com.geodeveloper.easypay

object Constants {
    //firebase paths
    const val charges =  "charges"
    const val apiAuth = "api_auth_credentials"
    const val live = "live"
    const val test = "test"
    const val users = "user"
    const val walletBalance = "wallet_balance"
    const val transactions = "transactions"
    const val paymentGatewayKey = "payment_gateway_key"
    const val publicKey = "publicKey"

    //keys
    const val airtime = "Airtime"
    const val data = "data"
    const val education =  "education"
    const val tv = "tv"
    const val electricity = "electricity"
    const val fundWallet = "fund wallet"

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


    //api responses
    const val TRANSACTION_SUCCESSFUL ="TRANSACTION SUCCESSFUL"
    const val delivered = "delivered"
    const val failed = "failed"
    const val unknown = "null"
    const val PENDING_CONFIRMATION = "Pending confirmation"
    const val WALLET_FUND = "WALLET FUND"
    const val FUND_TRANSFER = "FUND TRANSFER"
    const val cancel = "cancelled transaction"
    const val NGN = "NGN"


    //api transaction codes
    const val CODE_TRANSACTION_FAILED = "016"
    const val CODE_PRODCUT_NOT_EXTIST = "012"
    const val CODE_VARIATION_NOT_EXIST = "010"
    const val CODE_AMOUNT_BELOW_MINIMUM = "013"
    const val CODE_AMOUNT_ABOVE_MAXIMUM = "017"
    const val CODE_LOW_WALLET_BALANCE = "018"
    const val CODE_LIKELY_DUPLICATE_TRANSACTION = "019"
    const val CODE_BILLERS_SERVICE_UNREACHABLE = "030"
    const val CODE_SERVICE_SUSPENDED = "034"
    const val CODE_SERVICE_INACTIVE = "035"
    const val CODE_SYSTEM_ERROR = "083"
    const val CODE_INVALID_REQUEST_ID = "015"
    const val TRANSACTION_PROCESSED = "000"
    const val TRANSACTION_PROCESSING = "099"
    const val TRANSACTION_QUERY = "001"
    const val INVALID_ARGUMENT = "011"
    const val REQUEST_ID_ALREADY_EXIST = "014"
    const val INVALID_REQUEST_ID = "015"
    const val ACCOUNT_LOCKED = "021"
    const val ACCOUNT_SUSPENDED = "022"
    const val NOT_HAVE_API_ACCESS = "023"
    const val ACCOUNT_INACTIVE = "024"
    const val BILLER_NOT_REACHABLE = "030"
    const val BELOW_MINIUM_QTY = "031"
    const val ABOVE_MAXIMUM_QTY = "032"
    const val SERVICE_SUSPENDED = "034"
    const val SERVICE_INACTIVE = "035"
}