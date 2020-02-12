package at.fh.swengb.lunatschek

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthResponse(val token:String) {
}