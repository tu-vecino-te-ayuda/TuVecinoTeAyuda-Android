package org.tuvecinoteayuda.data.helprequests.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.tuvecinoteayuda.data.BaseRepository
import org.tuvecinoteayuda.data.CommonInterceptor
import org.tuvecinoteayuda.data.ResultWrapper
import org.tuvecinoteayuda.data.ServiceFactory
import org.tuvecinoteayuda.data.commons.models.MessageResponse
import org.tuvecinoteayuda.data.helprequests.HelpRequestApi
import org.tuvecinoteayuda.data.helprequests.models.HelpRequestResponse
import org.tuvecinoteayuda.data.helprequests.models.HelpRequestListResponse

class HelpRequestRepository(
    private val api: HelpRequestApi,
    private val dispatcher: CoroutineDispatcher
) : BaseRepository() {

    suspend fun getPendingRequestList(): ResultWrapper<HelpRequestResponse> {
        return safeApiCall(dispatcher) { api.pendingRequest() }
    }

    suspend fun getRequest(): ResultWrapper<HelpRequestListResponse> {
        return safeApiCall(dispatcher) { api.getRequest() }
    }

    suspend fun acceptRequest(id: String): ResultWrapper<HelpRequestListResponse> {
        return safeApiCall(dispatcher) { api.acceptRequest(id) }
    }

    suspend fun cancelAcceptedRequest(id: String): ResultWrapper<MessageResponse> {
        return safeApiCall(dispatcher) { api.cancelAcceptedRequest(id) }
    }

    suspend fun cancelMyRequest(id: String): ResultWrapper<MessageResponse> {
        return safeApiCall(dispatcher) { api.cancelMyRequest(id) }
    }

    companion object {
        fun newInstance(dispatcher: CoroutineDispatcher = Dispatchers.IO): HelpRequestRepository {
            return HelpRequestRepository(
                ServiceFactory.create(CommonInterceptor.newInstance()),
                dispatcher
            )
        }
    }
}