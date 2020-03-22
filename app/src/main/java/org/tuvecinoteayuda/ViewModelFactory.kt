package org.tuvecinoteayuda

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tuvecinoteayuda.data.login.repository.LoginRepository
import org.tuvecinoteayuda.data.regions.repository.RegionRepository
import org.tuvecinoteayuda.login.LoginViewModel
import org.tuvecinoteayuda.regions.RegionsViewModel
import org.tuvecinoteayuda.register.RegisterViewModel

class ViewModelFactory private constructor(
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(LoginRepository.newInstance())
                isAssignableFrom(RegisterViewModel::class.java) ->
                    RegisterViewModel(RegionRepository.newInstance())
                isAssignableFrom(RegionsViewModel::class.java) -> {
                    RegionsViewModel(RegionRepository.newInstance())
                }
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T

    companion object {

        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance() =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory()
                    .also { INSTANCE = it }
            }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
