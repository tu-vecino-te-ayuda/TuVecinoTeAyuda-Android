package org.tuvecinoteayuda.data.register.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.tuvecinoteayuda.data.*
import org.tuvecinoteayuda.data.commons.models.ActivityAreaTypeId
import org.tuvecinoteayuda.data.commons.models.AuthResponse
import org.tuvecinoteayuda.data.commons.models.NearByAreaTypeId
import org.tuvecinoteayuda.data.commons.models.UserTypeId
import org.tuvecinoteayuda.data.register.api.RegisterApi
import org.tuvecinoteayuda.data.register.models.RegisterUserRequest

class RegisterRepository private constructor(
    private val api: RegisterApi,
    private val dispatcher: CoroutineDispatcher,
    private val tokenProvider: TokenProvider
) : BaseRepository() {

    fun getNearByAreaType(): List<NearByAreaTypeId> {
        return listOf(
            NearByAreaTypeId.BUILDING,
            NearByAreaTypeId.NEIGHBORHOOD,
            NearByAreaTypeId.CITY,
            NearByAreaTypeId.OUTSIDE
        )
    }

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
        @NearByAreaTypeId.Companion.NearByAreaType nearbyAreasId: Int? = null,
        @ActivityAreaTypeId.Companion.ActivityAreaType activityAreaType: Int? = null // Only for Associations
    ): ResultWrapper<AuthResponse> {

        val request = RegisterUserRequest(
            name, email, phone, password, passwordConfirmation, address,
            city, state, zipCode, corporateName, cif, userTypeId, nearbyAreasId, activityAreaType
        )

        val register = safeApiCall(dispatcher) { api.registerUser(request) }

        when (register) {
            is ResultWrapper.Success -> tokenProvider.token = register.value.token
        }

        return register
    }

    companion object {
        fun newInstance(dispatcher: CoroutineDispatcher = Dispatchers.IO): RegisterRepository {
            return RegisterRepository(
                ServiceFactory.create(CommonInterceptor.newInstance(TokenProvider)),
                dispatcher,
                TokenProvider
            )
        }
    }
}
