package org.d3if3082.checknote.model
import com.google.gson.annotations.SerializedName

data class OpStatus(
    @SerializedName("name") var name: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("message") var message: String?
)
