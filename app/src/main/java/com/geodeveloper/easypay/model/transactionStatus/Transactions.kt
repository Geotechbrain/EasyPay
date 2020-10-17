package com.geodeveloper.easypay.models.transactionStatus

data class Transactions(
    val amount: Int?,
    val channel: String?,
    val commission: Int?,
    val convinience_fee: Int?,
    val discount: Any?,
    val email: String?,
    val method: String?,
    val name: Any?,
    val phone: String?,
    val platform: String?,
    val product_name: String?,
    val quantity: Int?,
    val service_verification: Any?,
    val status: String?,
    val total_amount: Int?,
    val transactionId: String?,
    val type: String?,
    val unique_element: String?,
    val unit_price: Int?
)