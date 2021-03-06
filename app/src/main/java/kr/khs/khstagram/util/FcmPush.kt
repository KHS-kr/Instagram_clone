package kr.khs.khstagram.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kr.khs.khstagram.navigation.model.PushDTO
import okhttp3.*
import java.io.IOException

class FcmPush {
    var JSON = MediaType.parse("application/json; charset=utf-8")
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AAAA5FS8Pis:APA91bHUfN45HAJp-KUpZEjNUUSIrE8oLk5krZhsOKowtdNpLopwmez8RWNxSS52jd8Z1i3_38TxOcQHLaYI9vhxdq-ASM2UHAvV6Gf7BYIu5XSzW-BY0CZgsdeLStEWDkZ15RHr51cq"
    var gson : Gson? = null
    var okHttpClient : OkHttpClient? = null

    companion object {
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid : String, title : String, message : String) {
        FirebaseFirestore.getInstance().collection("pushTokens").document(destinationUid).get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                var token = task.result?.get("pushToken").toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification.title = title
                pushDTO.notification.body = message

                var body = RequestBody.create(JSON, gson?.toJson(pushDTO))
                var request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=" + serverKey)
                    .url(url)
                    .post(body)
                    .build()

                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {

                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        println(response?.body()?.toString())
                    }

                })
            }
        }
    }
}