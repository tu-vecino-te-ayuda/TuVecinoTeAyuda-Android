package org.tuvecinoteayuda.requestdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tuvecinoteayuda.core.ui.ScreenState
import org.tuvecinoteayuda.core.util.Event
import org.tuvecinoteayuda.data.ResultWrapper
import org.tuvecinoteayuda.data.commons.models.MessageResponse
import org.tuvecinoteayuda.data.helprequests.models.HelpRequest
import org.tuvecinoteayuda.data.helprequests.repository.HelpRequestRepository
import org.tuvecinoteayuda.data.regions.models.City
import org.tuvecinoteayuda.data.regions.models.Region
import org.tuvecinoteayuda.data.regions.repository.RegionRepository

class HelpRequestDetailViewModel(
    private val requestRepository: HelpRequestRepository,
    private val regionRepository: RegionRepository
) : ViewModel() {

    // Screen State
    private val _screenState = MutableLiveData(ScreenState.INITIAL)
    val screenState: LiveData<ScreenState>
        get() = _screenState

    //Data
    private val _helpRequest = MutableLiveData<HelpRequest>()
    val helpRequest: LiveData<HelpRequest>
        get() = _helpRequest
    private val _state: MutableLiveData<Region> = MutableLiveData()
    val state: LiveData<Region>
        get() = _state
    private val _city: MutableLiveData<City> = MutableLiveData()
    val city: LiveData<City>
        get() = _city

    private val _helpRequestType = MutableLiveData<HelpRequestType>()
    val helpRequestType: LiveData<HelpRequestType>
        get() = _helpRequestType

    private val _showDelete = MutableLiveData<Boolean>(false)
    val showDelete: LiveData<Boolean>
        get() = _showDelete

    //Events
    private val _onAcceptRequestSuccessEvent = MutableLiveData<Event<Unit>>()
    val onAcceptRequestSuccessEvent: LiveData<Event<Unit>>
        get() = _onAcceptRequestSuccessEvent
    private val _onCancelRequestSuccessEvent = MutableLiveData<Event<String>>()
    val onCancelRequestSuccessEvent: LiveData<Event<String>>
        get() = _onCancelRequestSuccessEvent

    private val _onAcceptRequestErrorEvent = MutableLiveData<Event<String>>()
    val onAcceptRequestErrorEvent: LiveData<Event<String>>
        get() = _onAcceptRequestErrorEvent

    private var item: HelpRequest? = null

    fun start(
        requestId: String,
        helpRequestType: HelpRequestType
    ) {
        if (_screenState.value != ScreenState.INITIAL) return
        _helpRequestType.postValue(helpRequestType)
        getRequest(requestId)
    }

    private fun getRequest(requestId: String) {
        _screenState.value = ScreenState.LOADING_DATA
        viewModelScope.launch {
            val request = requestRepository.findRequestById(requestId)
            request?.let {
                item = it
                _helpRequest.postValue(it)
                _state.postValue(regionRepository.getRegionById(it.user.state))
                _city.postValue(regionRepository.getCityById(it.user.state, it.user.city))
                _screenState.postValue(ScreenState.DATA_LOADED)
                _showDelete.postValue(helpRequestType.value == HelpRequestType.REQUESTER || it.isAccepted())

            } ?: error("Invalid args")
        }
    }

    fun acceptRequest() {
        item?.also {
            _screenState.postValue(ScreenState.LOADING_DATA)
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = requestRepository.acceptHelpRequest(it.id)) {
                    is ResultWrapper.Success -> onAcceptedSuccess(result.value.data)
                    is ResultWrapper.GenericError -> onError(result.error?.message ?: "")
                    else -> onError("")
                }
            }
        }
    }

    fun cancelRequest() {
        item?.let { request ->
            _screenState.postValue(ScreenState.LOADING_DATA)
            viewModelScope.launch(Dispatchers.IO) {
                val result = if (helpRequestType.value == HelpRequestType.REQUESTER) {
                    requestRepository.cancelMyHelpRequest(request.id)
                } else {
                    requestRepository.cancelAcceptedHelpRequest(request.id)
                }
                when (result) {
                    is ResultWrapper.Success -> onCancelSuccess(result.value)
                    is ResultWrapper.GenericError -> onError(result.error?.message ?: "")
                    else -> onError("")
                }
            }
        }
    }

    private fun onAcceptedSuccess(request: HelpRequest) {
        item = request
        _helpRequest.postValue(request)
        _showDelete.postValue(helpRequestType.value == HelpRequestType.REQUESTER || request.isAccepted())
        _screenState.postValue(ScreenState.DATA_LOADED)
        _onAcceptRequestSuccessEvent.postValue(Event(Unit))
    }

    private fun onCancelSuccess(message: MessageResponse) {
        _screenState.postValue(ScreenState.DATA_LOADED)
        _onCancelRequestSuccessEvent.postValue(Event(message.message))
    }

    private fun onError(error: String) {
        _screenState.postValue(ScreenState.DATA_LOADED)
        _onAcceptRequestErrorEvent.postValue(Event(error))
    }
}
