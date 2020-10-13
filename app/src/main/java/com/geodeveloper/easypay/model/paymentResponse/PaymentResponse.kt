package com.geodeveloper.easypay.models.paymentResponse

data class PaymentResponse(
    val amount: String?,
    val code: String?,
    val purchased_code: String?,
    val requestId: String?,
    val response_description: String?,
    val transactionId: String?,
    val transaction_date: TransactionDate?
)