package org.tuvecinoteayuda.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.tuvecinoteayuda.R
import org.tuvecinoteayuda.ViewModelFactory
import org.tuvecinoteayuda.core.ext.removeErrorOnTyping
import org.tuvecinoteayuda.core.ext.showSnackBarError
import org.tuvecinoteayuda.core.ui.AutoCompleteAdapter
import org.tuvecinoteayuda.core.ui.ScreenState
import org.tuvecinoteayuda.core.util.observeEvent
import org.tuvecinoteayuda.data.commons.models.NearByAreaTypeId
import org.tuvecinoteayuda.data.regions.models.City
import org.tuvecinoteayuda.data.regions.models.Region
import org.tuvecinoteayuda.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private val args: RegisterFragmentArgs by navArgs()
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels { ViewModelFactory.getInstance() }

    private val areaAdapter by lazy { AutoCompleteAdapter<NearByAreaTypeId>(requireContext()) }
    private val regionsAdapter by lazy { AutoCompleteAdapter<Region>(requireContext()) }
    private val citiesAdapter by lazy { AutoCompleteAdapter<City>(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        FragmentRegisterBinding.inflate(inflater, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            area.setAdapter(areaAdapter)
            region.setAdapter(regionsAdapter)
            city.setAdapter(citiesAdapter)
            configureViews()
            setupListeners()
            return root
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeViewModelData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start(RegisterType.values()[args.registerType])
    }

    private fun configureViews() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        binding.toolbar.setTitle(R.string.register_title)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.registerButton.setOnButtonClickListener {
            viewModel.register()
        }
        binding.area.setOnItemClickListener { _, _, position, _ ->
            viewModel.area.value = areaAdapter.getItem(position)
        }
        binding.nameContainer.removeErrorOnTyping()
        binding.emailContainer.removeErrorOnTyping()
        binding.phoneContainer.removeErrorOnTyping()
        binding.passwordContainer.removeErrorOnTyping()
        binding.addressContainer.removeErrorOnTyping()
        binding.regionContainer.removeErrorOnTyping()
        binding.cityContainer.removeErrorOnTyping()
        binding.postalCodeContainer.removeErrorOnTyping()
    }

    private fun observeViewModelData() {
        observeScreenState()
        observeSpinnersData()
        observeFieldsErrors()
        observeEvents()
    }

    private fun observeScreenState() {
        viewModel.screenState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                ScreenState.LOADING_DATA -> binding.registerButton.showLoading()
                else -> binding.registerButton.hideLoading()
            }
        })
    }

    private fun observeSpinnersData() {
        viewModel.areas.observe(viewLifecycleOwner, Observer { areas ->
            areaAdapter.setData(areas)
        })
        viewModel.regions.observe(viewLifecycleOwner, Observer { regions ->
            regionsAdapter.setData(regions)
        })
        viewModel.cities.observe(viewLifecycleOwner, Observer { cities ->
            citiesAdapter.setData(cities)
        })
    }

    private fun observeFieldsErrors() {
        viewModel.nameError.observe(viewLifecycleOwner, Observer { error ->
            binding.nameContainer.error =
                if (error) getString(R.string.register_name_invalid) else null
        })
        viewModel.emailError.observe(viewLifecycleOwner, Observer { error ->
            binding.emailContainer.error =
                if (error) getString(R.string.register_email_invalid) else null
        })
        viewModel.phoneError.observe(viewLifecycleOwner, Observer { error ->
            binding.phoneContainer.error =
                if (error) getString(R.string.register_phone_invalid) else null
        })
        viewModel.passwordError.observe(viewLifecycleOwner, Observer { error ->
            binding.passwordContainer.error =
                if (error) getString(R.string.register_password_invalid) else null
        })
        viewModel.areaError.observe(viewLifecycleOwner, Observer { error ->
            binding.areaContainer.error =
                if (error) getString(R.string.register_area_invalid) else null
        })
        viewModel.regionError.observe(viewLifecycleOwner, Observer { error ->
            binding.regionContainer.error =
                if (error) getString(R.string.register_region_invalid) else null
        })
        viewModel.cityError.observe(viewLifecycleOwner, Observer { error ->
            binding.cityContainer.error =
                if (error) getString(R.string.register_city_invalid) else null
        })
        viewModel.addressError.observe(viewLifecycleOwner, Observer { error ->
            binding.addressContainer.error =
                if (error) getString(R.string.register_address_invalid) else null
        })
        viewModel.postalCodeError.observe(viewLifecycleOwner, Observer { error ->
            binding.postalCodeContainer.error =
                if (error) getString(R.string.register_postal_code_invalid) else null
        })
    }

    private fun observeEvents() {
        viewModel.onRegisterSuccessEvent.observeEvent(viewLifecycleOwner) {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFormFragmentToDashboardFragment())
        }
        viewModel.onRegisterFailedEvent.observeEvent(viewLifecycleOwner) {
            showSnackBarError(R.string.register_error)
        }
    }
}
