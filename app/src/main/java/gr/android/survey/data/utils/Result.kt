package gr.android.survey.data.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T?) : Result<T>()

    data class ServerError(val httpCode: Int, val errorCode: Int, val errorMessage: String?) :
        Result<Nothing>()

    data class NetworkError(val errorMessage: String?) : Result<Nothing>()

    data class ClientError(val httpCode: Int, val errorCode: Int, val errorMessage: String?) :
        Result<Nothing>()
}