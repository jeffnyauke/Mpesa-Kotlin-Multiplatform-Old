package io.piestack.multiplatform.mpesa.model.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LipaNaMpesaOnlinePaymentResponse(
    @SerialName("MerchantRequestID")
    val merchantRequestID: String,
    @SerialName("CheckoutRequestID")
    val checkoutRequestID: String,
    @SerialName("ResponseCode")
    val responseCode: String,
    @SerialName("ResponseDescription")
    val responseDescription: String,
    @SerialName("CustomerMessage")
    val customerMessage: String
)