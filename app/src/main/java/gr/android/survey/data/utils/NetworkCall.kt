package gr.android.survey.data.utils

import retrofit2.Response

suspend fun <T> call(
    serviceCall: suspend () -> Response<T>,
): Result<T> {
    return try {
        val response = serviceCall.invoke()
        if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            Result.ServerError(response.code(), -1, "Network error")
        }
    } catch (e: Exception) {
        Result.NetworkError(e)
    }
}