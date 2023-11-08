package com.mobatia.naisapp.constant

import com.google.gson.annotations.SerializedName


class GeneralSubmitResponseModel {
    @SerializedName("responsecode")
    var responseCode: String? = null

    @SerializedName("response")
    var response: ResponseData? = null

    class ResponseData {
        @SerializedName("response")
        var response: String? = null

        @SerializedName("statuscode")
        var statusCode: String? = null
    }
}