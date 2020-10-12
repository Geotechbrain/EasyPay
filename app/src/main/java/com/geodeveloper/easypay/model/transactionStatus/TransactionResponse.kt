package com.geodeveloper.easypay.models.transactionStatus

data class TransactionResponse(
    val amount: String?,
    val code: String?,
    val content: Content?,
    val purchased_code: String?,
    val requestId: String?,
    val response_description: String?,
    val transaction_date: TransactionDate?
)