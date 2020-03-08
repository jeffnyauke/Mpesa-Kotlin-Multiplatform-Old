package io.piestack.multiplatform.mpesa.model.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LipaNaMpesaOnlineQueryResponse(
    @SerialName("ResponseCode") val responseCode: Int,
    @SerialName("ResponseDescription") val responseDescription: String,
    @SerialName("MerchantRequestID") val merchantRequestID: String,
    @SerialName("CheckoutRequestID") val checkoutRequestID: String,
    @SerialName("ResultCode") val resultCode: Int,
    @SerialName("ResultDesc") val resultDesc: String
)