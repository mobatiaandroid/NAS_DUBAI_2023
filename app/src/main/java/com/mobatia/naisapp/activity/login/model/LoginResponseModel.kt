package com.mobatia.naisapp.activity.login.model

data class LoginResponseModel(
    var response: Response?,
    var responsecode: String?
) {
    data class Response(
        var data: ArrayList<String?>?,
        var response: String?,
        var responseArray: ResponseArray?,
        var statuscode: String?
    ) {
        data class ResponseArray(
            var token: String?,
            var userid: String?
        )
    }
}