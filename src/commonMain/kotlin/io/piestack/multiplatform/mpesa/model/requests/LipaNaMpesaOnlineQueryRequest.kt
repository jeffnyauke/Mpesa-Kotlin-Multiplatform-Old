package io.piestack.multiplatform.mpesa.model.requests

data class LipaNaMpesaOnlineQueryRequest(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val CheckoutRequestID: String
)