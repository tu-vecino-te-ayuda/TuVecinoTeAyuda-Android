package org.tuvecinoteayuda.data

import com.google.gson.Gson
import kotlinx.coroutines.*
import okio.IOException
import org.tuvecinoteayuda.data.commons.models.ErrorResponse
import retrofit2.HttpException

open class BaseRepository {

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(
                            null,
                            ErrorResponse(message = throwable.message ?: "")
                        )
                    }
                }
            }
        }
    }

    suspend fun <T> safeApiCallAsync(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): Deferred<ResultWrapper<T>> {
        return CoroutineScope(dispatcher).async {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(
                            null,
                            ErrorResponse(message = throwable.message ?: "")
                        )
                    }
                }
            }
        }
    }

    suspend fun <T> callAsync(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): Deferred<T> {
        return CoroutineScope(dispatcher).async {
            apiCall.invoke()
        }

    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            val string = throwable.response()?.errorBody()?.string()
            return Gson().fromJson(string, ErrorResponse::class.java)

        } catch (exception: Exception) {
            null
        }
    }
}