package at.fh.swengb.lunatschek

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthRequest(val  username:String, val password:String) {
}