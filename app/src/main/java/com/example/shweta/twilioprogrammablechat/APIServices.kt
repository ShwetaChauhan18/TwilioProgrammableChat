package com.example.shweta.twilioprogrammablechat

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface APIServices {
    /**
     * twilio login fetch token
     */
    @POST("public_api/twillio/token")
    fun getToken(@Body objectHashMap: HashMap<String, Any>): Observable<ResponseBody>
}