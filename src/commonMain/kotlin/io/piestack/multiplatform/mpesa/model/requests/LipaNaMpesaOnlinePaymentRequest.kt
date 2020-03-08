package io.piestack.multiplatform.mpesa.model.requests

data class LipaNaMpesaOnlinePaymentRequest(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val TransactionType: String,
    val Amount: String,
    val PhoneNumber: String,
    val PartyA: String,
    val PartyB: String,
    val CallBackURL: String,
    val AccountReference: String,
    val QueueTimeOutURL: String,
    val TransactionDesc: String
)