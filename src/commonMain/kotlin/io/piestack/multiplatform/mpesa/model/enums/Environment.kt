package io.piestack.multiplatform.mpesa.model.enums

enum class Environment(val baseUrl: String) {
    SANDBOX("https://sandbox.safaricom.co.ke"),
    PRODUCTION("https://api.safaricom.co.ke")
}