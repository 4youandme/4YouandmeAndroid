package com.foryouandme.data.repository.auth.network.request

import com.squareup.moshi.Json

data class PhoneNumberVerificationRequest(@Json(name = "user") val phone: PhoneNumberRequest)

data class PhoneNumberRequest(@Json(name = "phone_number") val phoneNumber: String)