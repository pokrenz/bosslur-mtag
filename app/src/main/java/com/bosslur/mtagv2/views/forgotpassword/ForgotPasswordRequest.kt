package com.bosslur.mtagv2.views.forgotpassword

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(

	@field:SerializedName("email")
	val email: String? = null
)
