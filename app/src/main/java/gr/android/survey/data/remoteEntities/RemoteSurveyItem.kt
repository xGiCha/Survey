package gr.android.survey.data.remoteEntities

import com.google.gson.annotations.SerializedName

data class RemoteSurveyItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("question")
    val question: String
)