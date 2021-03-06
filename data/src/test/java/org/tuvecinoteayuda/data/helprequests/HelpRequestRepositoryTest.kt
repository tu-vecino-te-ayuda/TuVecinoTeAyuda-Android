package org.tuvecinoteayuda.data.helprequests

import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.tuvecinoteayuda.data.ResultWrapper
import org.tuvecinoteayuda.data.commons.*
import org.tuvecinoteayuda.data.commons.models.MessageResponse
import org.tuvecinoteayuda.data.helprequests.models.AcceptHelpRequestResponse
import org.tuvecinoteayuda.data.helprequests.models.HelpRequestListResponse
import org.tuvecinoteayuda.data.helprequests.models.HelpRequestResponse
import org.tuvecinoteayuda.data.helprequests.models.HelpRequestTypeResponse
import org.tuvecinoteayuda.data.helprequests.repository.HelpRequestRepository

class HelpRequestRepositoryTest {

    private val repository = mockk<HelpRequestRepository>(relaxed = true)

    @Test
    fun `get pending request is success`() {
        runBlockingTest {
            val loginResponse =
                Gson().fromJson<HelpRequestResponse>(
                    PENDING_REQUESTS_RESPONSE_OK,
                    HelpRequestResponse::class.java
                )

            coEvery {
                repository.getPendingHelpRequests()
            } returns ResultWrapper.Success(loginResponse)

            val result = repository.getPendingHelpRequests()
            assertEquals(ResultWrapper.Success(loginResponse), result)
        }
    }

    @Test
    fun `get help request is success`() {
        runBlockingTest {
            val requestResponse =
                Gson().fromJson<HelpRequestListResponse>(
                    HELP_REQUESTS_RESPONSE_OK,
                    HelpRequestListResponse::class.java
                )

            coEvery {
                repository.getMyHelpRequests()
            } returns ResultWrapper.Success(requestResponse)

            val result = repository.getMyHelpRequests()
            assertEquals(ResultWrapper.Success(requestResponse), result)
        }
    }

    @Test
    fun `accept help request is success`() {
        runBlockingTest {
            val requestResponse =
                Gson().fromJson<AcceptHelpRequestResponse>(
                    ACCEPT_HELP_REQUESTS_RESPONSE_OK,
                    AcceptHelpRequestResponse::class.java
                )

            coEvery {
                repository.acceptHelpRequest(any())
            } returns ResultWrapper.Success(requestResponse)

            val result = repository.acceptHelpRequest("")
            assertEquals(ResultWrapper.Success(requestResponse), result)
        }
    }

    @Test
    fun `cancel help request is success`() {
        runBlockingTest {
            val requestResponse =
                Gson().fromJson<MessageResponse>(
                    CANCEL_HELP_REQUESTS_RESPONSE_OK,
                    MessageResponse::class.java
                )

            coEvery {
                repository.cancelAcceptedHelpRequest(any())
            } returns ResultWrapper.Success(requestResponse)

            val result = repository.cancelAcceptedHelpRequest("")
            assertEquals(ResultWrapper.Success(requestResponse), result)
        }
    }

    @Test
    fun `get help request types is success`() {
        runBlockingTest {
            val requestTypes =
                Gson().fromJson<HelpRequestTypeResponse>(
                    GET_REQUEST_TYPES_RESPONSE_OK,
                    HelpRequestTypeResponse::class.java
                )

            coEvery {
                repository.getHelpRequestTypes()
            } returns ResultWrapper.Success(requestTypes)

            val result = repository.getHelpRequestTypes()
            assertEquals(ResultWrapper.Success(requestTypes), result)
        }
    }
}