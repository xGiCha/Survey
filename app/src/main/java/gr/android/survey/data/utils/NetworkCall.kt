package gr.android.survey.data.utils

import retrofit2.Response

suspend fun <T> call(
    serviceCall: suspend () -> Response<T>,
): Result<T> {
    return try {
        val response = serviceCall.invoke()
        when (response.code()) {
            in 200 until 300 -> {
                if (response.isSuccessful) {
                    Result.Success(response.body())
                } else {
                    // Handle unexpected error codes within the 200-299 range
                    Result.ServerError(response.code(), -1, "Unexpected response code")
                }
            }
            400 -> Result.ClientError(response.code(), -1, "Bad Request")
            500 -> Result.ServerError(response.code(), -1, "Internal Server Error")
            else -> Result.ServerError(response.code(), -1, "Unexpected error")
        }
    } catch (e: Exception) {
        Result.NetworkError(e)
    }
}