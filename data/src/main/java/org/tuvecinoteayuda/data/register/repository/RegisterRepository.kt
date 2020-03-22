package org.tuvecinoteayuda.data.register.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.tuvecinoteayuda.data.BaseRepository
import org.tuvecinoteayuda.data.CommonInterceptor
import org.tuvecinoteayuda.data.ResultWrapper
import org.tuvecinoteayuda.data.ServiceFactory
import org.tuvecinoteayuda.data.commons.models.AuthResponse
import org.tuvecinoteayuda.data.commons.models.UserTypeId
import org.tuvecinoteayuda.data.register.api.RegisterApi
import org.tuvecinoteayuda.data.register.models.RegisterUserRequest

class RegisterRepository private constructor(
    private val api: RegisterApi,
    private val dispatcher: CoroutineDispatcher
) : BaseRepository() {

    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        passwordConfirmation: String,
        address: String,
        city: String,
        state: String,
        zipCode: String,
        corporateName: String? = null,
        cif: String? = null,
        @UserTypeId.Companion.UserType userTypeId: Int,
        @RegisterUserRequest.Companion.NearByAreaType nearbyAreasId: Int? = null,
        @RegisterUserRequest.Companion.ActivityAreaType activityAreaType: Int? = null
    ): ResultWrapper<AuthResponse> {

        val request = RegisterUserRequest(
            name, email, phone, password, passwordConfirmation, address,
            city, state, zipCode, corporateName, cif, userTypeId, nearbyAreasId, activityAreaType
        )

        return safeApiCall(dispatcher) { api.registerUser(request) }
    }

    companion object {
        fun newInstance(dispatcher: CoroutineDispatcher = Dispatchers.IO): RegisterRepository {
            return RegisterRepository(
                ServiceFactory.create(CommonInterceptor.newInstance()),
                dispatcher
            )
        }
    }
}